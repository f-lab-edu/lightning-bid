package com.lightningbid.common.exception;

import com.lightningbid.auction.domain.exception.ItemNotFoundException;
import com.lightningbid.common.dto.CommonResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<CommonResponseDto<Void>> handleIllegalArgumentException(IllegalArgumentException e) {

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

    // 클라이언트 JSON 형식 불일치
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<CommonResponseDto<Void>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {

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
