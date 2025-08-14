package com.lightningbid.item.exception;

import com.lightningbid.common.enums.ErrorCode;
import com.lightningbid.common.exception.BaseException;

public class ItemNotFoundException extends BaseException {

    public ItemNotFoundException() {
        super(ErrorCode.ITEM_NOT_FOUND);
    }
}
