package com.lightningbid.common.exception;

import com.lightningbid.common.dto.CommonResponseDto;
import com.lightningbid.common.enums.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<CommonResponseDto<String>> handleBaseException(BaseException e) {

        ErrorCode errorCode = e.getErrorCode();

        HttpStatus httpStatus = errorCode.getHttpStatus();
        String message = e.getMessage();
        String code = errorCode.getCode();

        return ResponseEntity.status(httpStatus).body(
                CommonResponseDto.error(httpStatus.value(), message, code)
        );
    }

    @ExceptionHandler({
            HttpMessageNotReadableException.class // 클라이언트 JSON 형식 불일치
            , OAuth2AuthenticationException.class
            , MethodArgumentTypeMismatchException.class // @RequestParam 형식 불일치
            , MissingServletRequestParameterException.class // @RequestParam 누락
    })
    public ResponseEntity<CommonResponseDto<Void>> handleIllegalArgumentException(Exception e) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                CommonResponseDto.error(HttpStatus.BAD_REQUEST.value(), e.getMessage())
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
