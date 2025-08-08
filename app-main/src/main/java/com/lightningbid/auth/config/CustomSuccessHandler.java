package com.lightningbid.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lightningbid.common.dto.CommonResponseDto;
import com.lightningbid.auth.enums.Role;
import com.lightningbid.auth.dto.CustomOAuth2User;
import com.lightningbid.auth.web.dto.response.LoginSuccessResponseDto;
import com.lightningbid.auth.web.dto.response.SignupTokenResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final Long refreshTokenExpirationMillis;

    private final JwtUtil jwtUtil;

    private final ObjectMapper objectMapper;

    public CustomSuccessHandler(@Value("${jwt.refresh-token-expiration-millis}")
                                Long refreshTokenExpirationMillis,
                                JwtUtil jwtUtil,
                                ObjectMapper objectMapper
    ) {

        this.jwtUtil = jwtUtil;
        this.refreshTokenExpirationMillis = refreshTokenExpirationMillis;
        this.objectMapper = objectMapper;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        CustomOAuth2User user = (CustomOAuth2User) authentication.getPrincipal();
        String role = authentication.getAuthorities().iterator().next().getAuthority();
        String username = user.getName();

        CommonResponseDto<?> responseDto;
        String redirectUri;
        if (Role.ROLE_GUEST.name().equals(role)) {
            String signupToken = jwtUtil.createSignupToken(username, role);
            redirectUri = "#access_token=" + signupToken;

            responseDto = CommonResponseDto.success(
                    HttpStatus.OK.value(),
                    "추가 정보 입력이 필요합니다.",
                    SignupTokenResponseDto
                            .builder()
                            .signupToken("Bearer " + signupToken)
                            .build()
            );

        } else {
            Long id = user.getId();
            String nickname = user.getNickname();
            String profileUrl = user.getProfileUrl();

            String accessToken = jwtUtil.createAccessToken(id, username, role, nickname, profileUrl);
            String refreshToken = jwtUtil.createRefreshToken(username, role);

            Duration tokenExp  = Duration.ofMillis(refreshTokenExpirationMillis);
            Duration cookieExp = tokenExp.plusMinutes(10);
            ResponseCookie cookie = ResponseCookie
                    .from("refreshToken", refreshToken)
                    .path("/")
                    .httpOnly(true)
                    //.secure(true)
                    //.sameSite("None")
                    .maxAge(cookieExp)
                    .build();
            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

            redirectUri = "#access_token=" + accessToken;
            responseDto = CommonResponseDto.success(
                    HttpStatus.OK.value(),
                    "로그인에 성공하였습니다.",
                    LoginSuccessResponseDto.builder().accessToken("Bearer " + accessToken).build()
            );
        }
//        getRedirectStrategy().sendRedirect(request, response, "http://localhost:3000" + redirectUri);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(objectMapper.writeValueAsString(responseDto));
        response.getWriter().flush();
    }
}