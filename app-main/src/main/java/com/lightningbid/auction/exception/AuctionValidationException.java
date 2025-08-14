package com.lightningbid.auction.exception;

import com.lightningbid.common.enums.ErrorCode;
import com.lightningbid.common.exception.BaseException;

public class AuctionValidationException extends BaseException {

    public AuctionValidationException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
