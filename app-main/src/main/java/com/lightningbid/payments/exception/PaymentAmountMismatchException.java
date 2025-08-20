package com.lightningbid.payments.exception;

import com.lightningbid.common.enums.ErrorCode;
import com.lightningbid.common.exception.BaseException;

public class PaymentAmountMismatchException extends BaseException {

    public PaymentAmountMismatchException() {

        super(ErrorCode.PAYMENT_AMOUNT_MISMATCH);
    }
}
