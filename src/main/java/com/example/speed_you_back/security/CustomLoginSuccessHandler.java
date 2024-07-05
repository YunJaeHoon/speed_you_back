package com.example.speed_you_back.security;

import com.example.speed_you_back.configuration.VersionProvider;
import com.example.speed_you_back.dto.ProfileDto;
import com.example.speed_you_back.dto.ResponseDto;
import com.example.speed_you_back.dto.TokenDto;
import com.example.speed_you_back.entity.Profile;
import com.example.speed_you_back.exception.CustomErrorCode;
import com.example.speed_you_back.exception.CustomException;
import com.example.speed_you_back.repository.ProfileRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
@Slf4j
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler
{
    @Autowired JwtUtil jwtUtil;
    @Autowired RedisTemplate<String, String> redisTemplate;
    @Autowired ProfileRepository profileRepository;
    @Autowired VersionProvider versionProvider;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException
    {
        // 로그인 이메일
        String email = authentication.getName();

        // body에서 remember-me 값 추출
        String rememberMeParam = request.getParameter("remember-me");
        boolean rememberMe = rememberMeParam != null && rememberMeParam.equalsIgnoreCase("true");

        Profile profile = profileRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(CustomErrorCode.LOGIN_FAILURE, email));

        ProfileDto.Profile profileDto = ProfileDto.Profile.builder()
                .profile_id(profile.getProfile_id())
                .email(profile.getEmail())
                .password(profile.getPassword())
                .username(profile.getUsername())
                .created_at(profile.getCreated_at())
                .role(profile.getRole())
                .build();

        // token 발행
        TokenDto tokenDto = jwtUtil.returnToken(profileDto, rememberMe);

        // redis에 access token 정보 저장
        redisTemplate.opsForValue().set(email, tokenDto.getAccessToken());

        ResponseDto.Success dto = ResponseDto.Success.builder()
                .message("로그인을 성공하였습니다.")
                .data(tokenDto)
                .version(versionProvider.getVersion())
                .build();

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(dto));
        response.getWriter().flush();
    }
}
