package com.lightningbid.item.exception;

import com.lightningbid.common.enums.ErrorCode;
import com.lightningbid.common.exception.BaseException;

public class ItemNotActiveException extends BaseException {

    public ItemNotActiveException() {
        super(ErrorCode.AUCTION_ITEM_INACTIVE);
    }
}
