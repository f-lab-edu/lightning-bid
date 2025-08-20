package com.lightningbid.payments.exception;

import com.lightningbid.common.enums.ErrorCode;
import com.lightningbid.common.exception.BaseException;

public class PaymentNotReadyException extends BaseException {

    public PaymentNotReadyException() {

        super(ErrorCode.PAYMENT_NOT_READY);
    }
}
