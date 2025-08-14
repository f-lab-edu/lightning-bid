package com.lightningbid.auction.domain.exception;

import com.lightningbid.common.enums.ErrorCode;
import com.lightningbid.common.exception.BaseException;

public class OverInstantSalePriceException extends BaseException {

    public OverInstantSalePriceException() {
        super(ErrorCode.BID_OVER_INSTANT);
    }
}
