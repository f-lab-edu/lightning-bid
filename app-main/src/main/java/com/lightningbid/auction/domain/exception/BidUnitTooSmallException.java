package com.lightningbid.auction.domain.exception;

import com.lightningbid.common.enums.ErrorCode;
import com.lightningbid.common.exception.BaseException;

public class BidUnitTooSmallException extends BaseException {

    public BidUnitTooSmallException() {
        super(ErrorCode.BID_UNIT_TOO_SMALL);
    }

    public BidUnitTooSmallException(String message) {
        super(message, ErrorCode.BID_UNIT_TOO_SMALL);
    }
}
