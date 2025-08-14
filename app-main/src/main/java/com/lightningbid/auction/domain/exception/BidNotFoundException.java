package com.lightningbid.auction.domain.exception;

import com.lightningbid.common.enums.ErrorCode;
import com.lightningbid.common.exception.BaseException;

public class BidNotFoundException extends BaseException {
    public BidNotFoundException() {
        super(ErrorCode.BID_NOT_FOUND);
    }
}
