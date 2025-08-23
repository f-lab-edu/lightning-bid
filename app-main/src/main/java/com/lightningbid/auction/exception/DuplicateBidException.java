package com.lightningbid.auction.exception;

import com.lightningbid.common.enums.ErrorCode;
import com.lightningbid.common.exception.BaseException;

public class DuplicateBidException extends BaseException {

    public DuplicateBidException() {

        super(ErrorCode.DUPLICATE_BID);
    }
}
