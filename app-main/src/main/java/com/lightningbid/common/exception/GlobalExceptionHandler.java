package com.lightningbid.common.exception;

import com.lightningbid.auction.domain.exception.ItemNotFoundException;
import com.lightningbid.auth.enums.TokenErrorCode;
import com.lightningbid.common.dto.CommonResponseDto;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MissingRequestCookieException.class)
    public ResponseEntity<CommonResponseDto<?>> handleMissingCookieException(MissingRequestCookieException e) {

        String message;
        String errorCode;
        HttpStatus httpStatus;

        if ("refreshToken".equals(e.getCookieName()) || "Authorization".equals(e.getCookieName())) {
            message = "리프레시 토큰이 존재하지 않습니다.";
            errorCode = TokenErrorCode.REFRESH_TOKEN_EMPTY.name();
            httpStatus = HttpStatus.UNAUTHORIZED;
        } else {
            message = String.format("필수 쿠키 '%s'가 누락되었습니다.", e.getCookieName());
            errorCode = "COOKIE_EMPTY";
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return ResponseEntity.status(httpStatus).body(
                CommonResponseDto.error(httpStatus.value(), message, errorCode)
        );
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<CommonResponseDto<String>> handleJwtException(JwtException e) {

        String message;
        String errorCode;
        if (e instanceof ExpiredJwtException) {
            message = "토큰이 만료되었습니다.";
            errorCode = TokenErrorCode.REFRESH_TOKEN_EXPIRED.name();
        } else {
            message = "유효하지 않은 토큰입니다.";
            errorCode = TokenErrorCode.REFRESH_TOKEN_INVALID.name();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                CommonResponseDto.error(HttpStatus.UNAUTHORIZED.value(), message, errorCode)
        );
    }

    @ExceptionHandler({
            IllegalArgumentException.class
            , IllegalStateException.class
            , HttpMessageNotReadableException.class // 클라이언트 JSON 형식 불일치
            , OAuth2AuthenticationException.class
    })
    public ResponseEntity<CommonResponseDto<Void>> handleIllegalArgumentException(RuntimeException e) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                CommonResponseDto.error(HttpStatus.BAD_REQUEST.value(), e.getMessage())
        );
    }

    @ExceptionHandler(ItemNotFoundException.class)
    public ResponseEntity<CommonResponseDto<Void>> handleResourceNotFoundException(ItemNotFoundException e) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                CommonResponseDto.error(HttpStatus.NOT_FOUND.value(), e.getMessage())
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResponseDto<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {

        List<String> errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getDefaultMessage() + " (" + error.getField() + ")")
                .toList();
        String errorMessage = errors.isEmpty() ? "필수값을 확인하시기 바랍니다." : errors.getFirst();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                CommonResponseDto.error(HttpStatus.BAD_REQUEST.value(), errorMessage)
        );
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponseDto<Void>> handleGlobalException(Exception e) {

        log.error("Unhandled exception occurred: ", e);

        String message = "서버 오류가 발생했습니다. 관리자에게 문의해주세요.";

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                CommonResponseDto.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), message)
        );
    }
}
