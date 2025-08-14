package com.lightningbid.auction.domain.exception;

import com.lightningbid.common.enums.ErrorCode;
import com.lightningbid.common.exception.BaseException;

public class BidAmountTooLowException extends BaseException {

    public BidAmountTooLowException() {
        super(ErrorCode.BID_PRICE_TOO_LOW);
    }
}
