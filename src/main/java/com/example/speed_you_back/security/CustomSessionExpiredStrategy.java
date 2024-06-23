package com.example.speed_you_back.security;

import com.example.speed_you_back.configuration.VersionProvider;
import com.example.speed_you_back.dto.ResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomSessionExpiredStrategy implements SessionInformationExpiredStrategy
{

    @Autowired
    VersionProvider versionProvider;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onExpiredSessionDetected(SessionInformationExpiredEvent event) throws IOException, ServletException {

        HttpServletRequest request = event.getRequest();
        HttpServletResponse response = event.getResponse();

        ResponseDto.Error dto = ResponseDto.Error.builder()
                .data(null)
                .message("다른 기기에서 로그인하여 세션이 만료되었습니다.")
                .version(versionProvider.getVersion())
                .build();

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(dto));
        response.getWriter().flush();
    }
}
