package com.example.speed_you_back.security;

import com.example.speed_you_back.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Configuration
@EnableWebSecurity
public class SecurityConfig
{
    @Autowired JwtUtil jwtUtil;
    @Autowired RedisTemplate<String, String> redisTemplate;
    @Autowired ProfileRepository profileRepository;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity,
                                                   CustomUserDetailService customUserDetailService) throws Exception
    {
        // 기본 설정
        httpSecurity
                .httpBasic(HttpBasicConfigurer::disable)    // HTTP 기본 인증 비활성화
                .csrf(CsrfConfigurer::disable);             // CSRF 보호 비활성화

        // 경로별 권한 설정
        httpSecurity
                .authorizeHttpRequests((requests) -> requests
                        // 해당 요청은 모든 사용자에게 접근 권한 허용
                        .requestMatchers("/login/**", "/join/**", "/reset-password").permitAll()
                        .requestMatchers("/api/login/**", "/api/join/**", "/api/reset-password").permitAll()
                        .requestMatchers("/game/**").permitAll()
                        .requestMatchers("/api/game/**").permitAll()
                        // 해당 요청은 인증된 사용자에게만 접근 권한 허용
                        .requestMatchers("/api/get-role").hasAnyRole("ADMIN", "USER")
                        // 해당 요청은 관리자에게만 접근 권한 허용
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        // 그 외의 요청은 인증된 사용자에게만 허용
                        .anyRequest().authenticated()
                );

        // 예외 처리 설정
        httpSecurity
                .exceptionHandling((exception) -> exception
                        .authenticationEntryPoint(customAuthenticationEntryPoint())
                        .accessDeniedHandler(customAccessDeniedHandler())
                );

        // UsernamePasswordAuthenticationFilter 앞에 JwtAuthFilter 추가
        httpSecurity
                .addFilterBefore(new JwtAuthFilter(customUserDetailService, jwtUtil, redisTemplate, profileRepository), UsernamePasswordAuthenticationFilter.class);

        // 로그인, 로그아웃, 로그인 처리 서비스 설정
        httpSecurity
                .formLogin((login) -> login
                        .usernameParameter("email")         // 아이디
                        .passwordParameter("password")      // 비밀번호
                        .loginProcessingUrl("/api/login")   // 로그인 처리 경로
                        .permitAll()
                        .defaultSuccessUrl("/", true)       // 로그인 성공 시, 이동 경로
                        .successHandler(customLoginSuccessHandler())
                        .failureHandler(customLoginFailureHandler())
                )
                .logout((logout) -> logout
                        .logoutUrl("/api/logout")
                        .logoutSuccessHandler(customLogoutSuccessHandler())
                        .deleteCookies("remember-me")
                )
                .userDetailsService(customUserDetailService);   // 로그인 처리 서비스

        // remember-me 설정
        httpSecurity
                .rememberMe((rememberConfig) -> rememberConfig
                        .key("Test-Key-For-Speed_you")
                        .tokenValiditySeconds(60 * 60 * 24 * 30)    // 30일
                        .rememberMeParameter("remember-me")
                        .userDetailsService(customUserDetailService)
                );

        // 세션 생성 및 사용 정지
        httpSecurity
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        return httpSecurity.build();
    }

    @Bean
    public CustomAuthenticationEntryPoint customAuthenticationEntryPoint() {
        return new CustomAuthenticationEntryPoint();
    }

    @Bean
    public CustomAccessDeniedHandler customAccessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }

    @Bean
    public CustomLoginSuccessHandler customLoginSuccessHandler() {
        return new CustomLoginSuccessHandler();
    }

    @Bean
    public CustomLoginFailureHandler customLoginFailureHandler() {
        return new CustomLoginFailureHandler();
    }

    @Bean
    public CustomLogoutSuccessHandler customLogoutSuccessHandler() {
        return new CustomLogoutSuccessHandler();
    }

    // 세션 생성, 만료 이벤트 리스너
    @Bean
    public static ServletListenerRegistrationBean<HttpSessionEventPublisher> httpSessionEventPublisher() {
        return new ServletListenerRegistrationBean<>(new HttpSessionEventPublisher());
    }
}
