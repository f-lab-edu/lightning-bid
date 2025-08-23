package com.lightningbid.payments.exception;

import com.lightningbid.common.enums.ErrorCode;
import com.lightningbid.common.exception.BaseException;
import lombok.Getter;

@Getter
public class TossPaymentException extends BaseException {

    private String code;

    public TossPaymentException(String message, ErrorCode errorCode) {

        super(message, errorCode);
    }

    public TossPaymentException(String code, String message) {

        super(message, ErrorCode.PAYMENT_BAD_REQUEST);
        this.code = code;
    }
}
