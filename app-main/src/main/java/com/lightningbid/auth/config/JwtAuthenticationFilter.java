package com.lightningbid.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lightningbid.auth.dto.CustomOAuth2User;
import com.lightningbid.auth.dto.UserDto;
import com.lightningbid.auth.enums.Role;
import com.lightningbid.auth.enums.TokenErrorCode;
import com.lightningbid.common.dto.CommonResponseDto;
import com.lightningbid.common.enums.ErrorCode;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        // 로그아웃 및 토큰 재발급 경로는 엑세스 토큰이 있어도 검증을 건너뛴다.
        if (requestURI.endsWith("/auth/logout") || requestURI.endsWith("/auth/refresh")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorizationHeader.replace("Bearer ", "");

        // 토큰 유효성 검증
        try {
            jwtUtil.verifyToken(token);

        } catch (ExpiredJwtException e) {
            // 토큰이 만료된 경우
            log.warn("토큰이 만료되었습니다: {}", e.getMessage());
            response.setHeader(
                    HttpHeaders.WWW_AUTHENTICATE,
                    "Bearer error=\"invalid_token\", error_description=\"The access token expired\""
            );

            sendErrorResponse(
                    response,
                    ErrorCode.ACCESS_TOKEN_EXPIRED.getHttpStatus().value(),
                    ErrorCode.ACCESS_TOKEN_EXPIRED.getMessage(),
                    ErrorCode.ACCESS_TOKEN_EXPIRED.getCode()
            );
            return;

        } catch (JwtException | IllegalArgumentException e) {
            // 그 외 JWT 관련 예외 (서명 오류, 형식 오류 등)
            log.warn("유효하지 않은 토큰입니다: {}", e.getMessage());
            response.setHeader(
                    HttpHeaders.WWW_AUTHENTICATE,
                    "Bearer error=\"invalid_token\", error_description=\"The token is malformed or invalid\""
            );

            sendErrorResponse(
                    response,
                    ErrorCode.ACCESS_TOKEN_INVALID.getHttpStatus().value(),
                    ErrorCode.ACCESS_TOKEN_INVALID.getMessage(),
                    ErrorCode.ACCESS_TOKEN_INVALID.getCode()
            );
            return;
        }

        // TODO 리프래시 토큰으로 요청시 거절 개발

        // 토큰에서 권한 추출
        String roleStr = jwtUtil.getRole(token);

        // 권한에 따른 접근 제어
        if (Role.ROLE_GUEST.name().equals(roleStr)) {
            // GUEST 권한은 회원가입 API 에만 접근가능
            if (!requestURI.endsWith("/auth/signUp/social")) {
                log.warn("GUEST 권한으로 허용되지 않은 경로에 접근 시도: {}", requestURI);
                sendErrorResponse(
                        response,
                        ErrorCode.ACCESS_TOKEN_FORBIDDEN.getHttpStatus().value(),
                        ErrorCode.ACCESS_TOKEN_FORBIDDEN.getMessage(),
                        ErrorCode.ACCESS_TOKEN_FORBIDDEN.getCode()
                );

                return;
            }
        }

        // (접근이 허용된 경우) Spring Security 인증 토큰 생성
        String username = jwtUtil.getUsername(token);
        String profileUrl = jwtUtil.getProfileUrl(token);
        String nickname = jwtUtil.getNickname(token);
        Long id = jwtUtil.getId(token);

        CustomOAuth2User customOAuth2User = CustomOAuth2User.builder()
                .userDto(UserDto.builder()
                        .username(username)
                        .id(id)
                        .profileUrl(profileUrl)
                        .nickname(nickname)
                        .role(Role.valueOf(roleStr))
                        .build())
                .build();

        Authentication authToken = new UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }

    private void sendErrorResponse(HttpServletResponse response, int status, String message, String errorCode) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        String result = objectMapper.writeValueAsString(
                CommonResponseDto.error(status, message, errorCode)
        );
        response.getWriter().write(result);
    }
}
