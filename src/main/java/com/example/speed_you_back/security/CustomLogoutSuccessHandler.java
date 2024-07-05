package com.example.speed_you_back.security;

import com.example.speed_you_back.configuration.VersionProvider;
import com.example.speed_you_back.dto.ResponseDto;
import com.example.speed_you_back.entity.Profile;
import com.example.speed_you_back.exception.CustomErrorCode;
import com.example.speed_you_back.exception.CustomException;
import com.example.speed_you_back.repository.ProfileRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;

@Component
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler
{
    @Autowired JwtUtil jwtUtil;
    @Autowired RedisTemplate<String, String> redisTemplate;
    @Autowired ProfileRepository profileRepository;
    @Autowired VersionProvider versionProvider;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException
    {
        // 로그아웃 성공 여부
        boolean isSuccess = false;

        String errorCode = null;
        String errorMessage = null;

        String authorizationHeader = request.getHeader("Authorization");

        // 헤더에 access token이 존재하는지 체크
        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer "))
        {
            String token = authorizationHeader.substring(7);

            // access token 유효성 검증
            if(jwtUtil.validateToken(token)) {
                Long userId = jwtUtil.getProfileId(token);

                Profile profile = profileRepository.findById(userId)
                        .orElseThrow(() -> new CustomException(CustomErrorCode.EMAIL_NOT_FOUND, null));

                String email = profile.getEmail();

                // redis에서 token 정보 삭제
                if (Objects.equals(redisTemplate.opsForValue().get(email), token)) {
                    isSuccess = true;
                    redisTemplate.delete(email);
                }
                else {
                    errorCode = "EXPIRED_TOKEN";
                    errorMessage = "이미 만료된 토큰입니다.";
                }
            }
            else {
                errorCode = "INVALID_TOKEN";
                errorMessage = "유효하지 않은 토큰입니다.";
            }
        }
        else {
            errorCode = "TOKEN_NOT_FOUND";
            errorMessage = "헤더에 토큰 정보가 존재하지 않습니다.";
        }

        if(isSuccess) {
            ResponseDto.Success dto = ResponseDto.Success.builder()
                    .data(null)
                    .message("로그아웃을 성공하였습니다.")
                    .version(versionProvider.getVersion())
                    .build();

            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(dto));
            response.getWriter().flush();
        }
        else {
            ResponseDto.Error dto = ResponseDto.Error.builder()
                    .code(errorCode)
                    .message(errorMessage)
                    .data(null)
                    .version(versionProvider.getVersion())
                    .build();

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(dto));
            response.getWriter().flush();
        }
    }
}
