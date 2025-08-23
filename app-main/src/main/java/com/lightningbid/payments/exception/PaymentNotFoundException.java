package com.lightningbid.payments.exception;

import com.lightningbid.common.enums.ErrorCode;
import com.lightningbid.common.exception.BaseException;

public class PaymentNotFoundException extends BaseException {

    public PaymentNotFoundException() {

        super(ErrorCode.PAYMENT_NOT_FOUND);
    }
}
