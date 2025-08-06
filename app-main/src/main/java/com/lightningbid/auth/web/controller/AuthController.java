package com.lightningbid.auth.web.controller;

import com.lightningbid.auth.web.dto.response.LoginSuccessResponseDto;
import com.lightningbid.auth.dto.TokensDto;
import com.lightningbid.common.dto.CommonResponseDto;
import com.lightningbid.auth.service.AuthService;
import com.lightningbid.user.web.dto.request.SignupRequestDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/auth")
public class AuthController {

    @Value("${jwt.refresh-token-expiration-millis}")
    private final Long refreshTokenExpirationMillis;

    private final AuthService authService;

    public AuthController(
            @Value("${jwt.refresh-token-expiration-millis}")
            Long refreshTokenExpirationMillis,
            AuthService authService) {

        this.refreshTokenExpirationMillis = refreshTokenExpirationMillis;
        this.authService = authService;
    }

    @PostMapping("/signup/social")
    public ResponseEntity<CommonResponseDto<LoginSuccessResponseDto>> completeSocialSignup(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                                                           @RequestBody SignupRequestDto requestDto) {

        TokensDto tokens = authService.completeSocialSignup(authorizationHeader, requestDto);

        return getCommonResponseDtoResponseEntity(tokens, "회원 가입이 완료 되었습니다.");
    }

    @PostMapping("/refresh")
    public ResponseEntity<CommonResponseDto<LoginSuccessResponseDto>> refreshAccessToken(
            @CookieValue(name = "refreshToken")
            String refreshToken) {

        TokensDto tokens = authService.refreshAccessToken(refreshToken);
        return getCommonResponseDtoResponseEntity(tokens, "토큰 재발급이 완료 되었습니다.");
    }

    private ResponseEntity<CommonResponseDto<LoginSuccessResponseDto>> getCommonResponseDtoResponseEntity(TokensDto tokens, String message) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", tokens.getRefreshToken())
                .path("/")
                .httpOnly(true)
                //.secure(true)
                //.sameSite("Strict")
                .maxAge(refreshTokenExpirationMillis / 1000)
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

        // ResponseCookie 빌더를 사용하여 쿠키를 만료시킵니다.
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
