package com.lightningbid.item.exception;

import com.lightningbid.common.enums.ErrorCode;
import com.lightningbid.common.exception.BaseException;

public class CategoryIdNotFoundException extends BaseException {

    public CategoryIdNotFoundException() {
        super(ErrorCode.CATEGORY_ID_NOT_FOUND);
    }
}
