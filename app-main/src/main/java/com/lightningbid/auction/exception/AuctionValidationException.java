package com.lightningbid.auction.exception;

import com.lightningbid.common.enums.ErrorCode;
import com.lightningbid.common.exception.BaseException;

public class AuctionValidationException extends BaseException {

    public AuctionValidationException() {
        super(ErrorCode.DEPOSIT_REQUIRED);
    }

    public AuctionValidationException(String message) {
        super(message, ErrorCode.DEPOSIT_REQUIRED);
    }

    public AuctionValidationException(String message, ErrorCode errorCode) {
        super(message, ErrorCode.DEPOSIT_REQUIRED);
    }
}
