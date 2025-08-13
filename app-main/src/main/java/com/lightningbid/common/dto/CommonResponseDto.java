package com.lightningbid.common.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CommonResponseDto<T> {
    private final Integer status;
    private final String message;
    private final T data;

    public static <T> CommonResponseDto<T> success(Integer status, String message, T data) {
        return new CommonResponseDto<>(status, message, data);
    }

    public static CommonResponseDto<Void> success(Integer status, String message) {
        return new CommonResponseDto<>(status, message, null);
    }

    public static CommonResponseDto<Void> error(Integer status, String message) {
        return new CommonResponseDto<>(status, message, null);
    }

    public static <T> CommonResponseDto<T> error(Integer status, String message, T errorCode) {
        return new CommonResponseDto<> (status, message, errorCode);
    }
}
