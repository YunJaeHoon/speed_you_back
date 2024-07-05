package com.example.speed_you_back.security;

import com.example.speed_you_back.configuration.VersionProvider;
import com.example.speed_you_back.dto.ResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomLoginFailureHandler implements AuthenticationFailureHandler
{
    @Autowired VersionProvider versionProvider;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException
    {
        String errorCode = null;
        String errorMessage = null;

        if (exception instanceof BadCredentialsException || exception instanceof InternalAuthenticationServiceException) {
            // 이메일 또는 비밀번호가 틀린 경우
            errorCode = "WRONG";
            errorMessage = "이메일 또는 비밀번호가 틀렸습니다.";
        }
        else if (exception instanceof DisabledException) {
            // 해당 계정이 비활성화된 경우
            errorCode = "DISABLED";
            errorMessage = "해당 계정이 비활성화 되었습니다.";
        }
        else {
            // 예기치 못한 에러가 발생한 경우
            errorCode = "UNEXPECTED";
            errorMessage = exception.getMessage();
        }

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
