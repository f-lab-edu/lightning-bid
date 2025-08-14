package com.lightningbid.auction.exception;

import com.lightningbid.common.enums.ErrorCode;
import com.lightningbid.common.exception.BaseException;

public class AuctionNotFoundException extends BaseException {

    public AuctionNotFoundException() {
        super(ErrorCode.AUCTION_NOT_FOUND);
    }
}
