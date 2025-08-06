package com.lightningbid.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;

    private final CustomSuccessHandler customSuccessHandler;

    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    private final JwtUtil jwtUtil;

    private final ObjectMapper objectMapper;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // CSRF, Form Login, HTTP Basic 인증 비활성화
        http.csrf(AbstractHttpConfigurer::disable);
        http.formLogin(AbstractHttpConfigurer::disable);
        http.httpBasic(AbstractHttpConfigurer::disable);

        // JWT 검증 필터
        http.addFilterBefore(new JwtAuthenticationFilter(jwtUtil, objectMapper), UsernamePasswordAuthenticationFilter.class);

        // OAuth2 로그인 설정
        http.oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig.userService(customOAuth2UserService))
                .successHandler(customSuccessHandler)
        );

        // 경로별 인가 설정
        http.authorizeHttpRequests((auth) -> auth
                .requestMatchers("/", "/login", "/v1/auth/signup/social", "/v1/auth/refresh", "/v1/auth/logout").permitAll()
//                .requestMatchers(HttpMethod.GET, "/v1/items/*").permitAll()
                .anyRequest().authenticated()
        );

        // 세션 관리 설정을 STATELESS로 변경 (JWT 인증 방식을 사용하기 위함)
        http.sessionManagement((session) -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.exceptionHandling((exceptions) -> exceptions
                .authenticationEntryPoint(customAuthenticationEntryPoint)
        );

        return http.build();
    }
}
