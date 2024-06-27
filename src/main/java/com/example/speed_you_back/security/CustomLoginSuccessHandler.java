package com.example.speed_you_back.security;

import com.example.speed_you_back.configuration.VersionProvider;
import com.example.speed_you_back.dto.ProfileDto;
import com.example.speed_you_back.dto.ResponseDto;
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

import java.io.IOException;

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
        String email = authentication.getName();

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

        // access token 발행
        String accessToken = jwtUtil.createAccessToken(profileDto);

        // redis에 token 정보 저장
        redisTemplate.opsForValue().set(email, accessToken);

        ResponseDto.Success dto = ResponseDto.Success.builder()
                .data(accessToken)
                .message("로그인을 성공하였습니다.")
                .version(versionProvider.getVersion())
                .build();

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(dto));
        response.getWriter().flush();
    }
}
