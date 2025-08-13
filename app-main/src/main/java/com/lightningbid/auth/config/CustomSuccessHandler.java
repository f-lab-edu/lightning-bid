package com.lightningbid.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lightningbid.auth.enums.Role;
import com.lightningbid.auth.dto.CustomOAuth2User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        CustomOAuth2User user = (CustomOAuth2User) authentication.getPrincipal();
        String role = authentication.getAuthorities().iterator().next().getAuthority();

        String signToken = jwtUtil.createSignToken(user.getName(), role);

        ResponseCookie cookie;
        String redirectUri;

        if (Role.ROLE_GUEST.name().equals(role)) {
            cookie = createCookie("signUpToken", signToken);
            redirectUri = "/signUp";
        } else {
            cookie = createCookie("signInToken", signToken);
            redirectUri = "/signIn";
        }

        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        getRedirectStrategy().sendRedirect(request, response, "http://localhost:3000" + redirectUri);
    }

    private ResponseCookie createCookie(String tokenName, String signToken) {

        return ResponseCookie
                .from(tokenName, signToken)
                .path("/")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .maxAge(720)
                .build();
    }
}