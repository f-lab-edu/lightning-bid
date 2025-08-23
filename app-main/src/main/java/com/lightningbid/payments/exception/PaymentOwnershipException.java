package com.lightningbid.payments.exception;

import com.lightningbid.common.enums.ErrorCode;
import com.lightningbid.common.exception.BaseException;

public class PaymentOwnershipException extends BaseException {

    public PaymentOwnershipException() {
        super(ErrorCode.PAYMENT_OWNERSHIP_MISMATCH);
    }
}
