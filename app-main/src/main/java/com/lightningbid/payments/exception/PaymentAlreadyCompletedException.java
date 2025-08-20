package com.lightningbid.payments.exception;

import com.lightningbid.common.enums.ErrorCode;
import com.lightningbid.common.exception.BaseException;

public class PaymentAlreadyCompletedException extends BaseException {

    public PaymentAlreadyCompletedException() {

        super(ErrorCode.PAYMENT_ALREADY_COMPLETED);
    }
}
