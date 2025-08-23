package com.lightningbid.auth.web.controller;

import com.lightningbid.auth.web.dto.response.LoginSuccessResponseDto;
import com.lightningbid.auth.dto.TokensDto;
import com.lightningbid.common.config.properties.JwtProperties;
import com.lightningbid.common.dto.CommonResponseDto;
import com.lightningbid.auth.service.AuthService;
import com.lightningbid.user.web.dto.request.SignupRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/auth")
public class AuthController {

    private final AuthService authService;

    private final JwtProperties jwtProperties;

    @PostMapping("/signUp/social")
    public ResponseEntity<CommonResponseDto<LoginSuccessResponseDto>> completeSocialSignup(
            @CookieValue(name = "signUpToken", required = false)
            String signUpToken,
            @RequestBody SignupRequestDto requestDto) {

        return getCommonResponseDtoResponseEntity(authService.completeSocialSignUp(signUpToken, requestDto), "회원 가입이 완료 되었습니다.");
    }

    @PostMapping("/signIn/social")
    public ResponseEntity<CommonResponseDto<LoginSuccessResponseDto>> completeSocialSignIn(
            @CookieValue(name = "signInToken", required = false)
            String signInToken) {

        return getCommonResponseDtoResponseEntity(authService.completeSocialSignIn(signInToken), "로그인 완료 되었습니다.");
    }

    @PostMapping("/refresh")
    public ResponseEntity<CommonResponseDto<LoginSuccessResponseDto>> refreshAccessToken(
            @CookieValue(name = "refreshToken", required = false)
            String refreshToken) {

        TokensDto tokens = authService.refreshAccessToken(refreshToken);
        return getCommonResponseDtoResponseEntity(tokens, "토큰 재발급이 완료 되었습니다.");
    }

    private ResponseEntity<CommonResponseDto<LoginSuccessResponseDto>> getCommonResponseDtoResponseEntity(TokensDto tokens, String message) {

        Duration tokenExp = Duration.ofMillis(jwtProperties.getRefreshTokenExpirationMillis());
        Duration cookieExp = tokenExp.plusMinutes(10);
        ResponseCookie cookie = ResponseCookie.from("refreshToken", tokens.getRefreshToken())
                .path("/")
                .httpOnly(true)
                //.secure(true)
                //.sameSite("Strict")
                .maxAge(cookieExp)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(CommonResponseDto.success(
                        HttpStatus.OK.value(),
                        message,
                        LoginSuccessResponseDto
                                .builder()
                                .accessToken("Bearer " + tokens.getAccessToken())
                                .build()));
    }

    @PostMapping("/logout")
    public ResponseEntity<CommonResponseDto<Void>> logout() {

        ResponseCookie expiredCookie = ResponseCookie.from("refreshToken", null)
                .maxAge(0)
                .path("/")
                .httpOnly(true)
                //.secure(true)
                //.sameSite("Strict")
                .build();

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, expiredCookie.toString())
                .body(CommonResponseDto.success(HttpStatus.OK.value(), "로그아웃 되었습니다."));
    }
}
