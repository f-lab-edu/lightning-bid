package com.lightningbid.auction.domain.exception;

import com.lightningbid.common.enums.ErrorCode;
import com.lightningbid.common.exception.BaseException;

public class DepositRequiredException extends BaseException {

    public DepositRequiredException() {
        super(ErrorCode.DEPOSIT_REQUIRED);
    }
}
