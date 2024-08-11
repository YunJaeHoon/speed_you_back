package com.example.speed_you_back.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        
        // CORS 설정
        registry
                .addMapping("/**")                                     // 해당 설정을 적용할 URL
                .allowedOrigins("https://speed-you.netlify.app", "http://localhost:3000")    // 자원 공유를 허락할 기기의 주소
                .allowedMethods("GET", "POST", "PUT", "DELETE")        // 허용할 HTTP method
                .allowCredentials(true)                                // 쿠키 요청 허용
                .allowedHeaders("*");                                  // CORS 요청에 허용된 헤더

    }

}
