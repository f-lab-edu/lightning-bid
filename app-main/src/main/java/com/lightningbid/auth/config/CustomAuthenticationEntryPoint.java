package com.lightningbid.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lightningbid.auth.enums.TokenErrorCode;
import com.lightningbid.common.dto.CommonResponseDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        CommonResponseDto<String> errorResponse = CommonResponseDto.error(
                HttpStatus.UNAUTHORIZED.value(),
                "인증이 필요합니다.",
                TokenErrorCode.TOKEN_EMPTY.name()
        );

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}