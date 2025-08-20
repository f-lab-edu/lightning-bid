package com.lightningbid.auth.exception;

import com.lightningbid.common.enums.ErrorCode;
import com.lightningbid.common.exception.BaseException;

public class RefreshTokenMissingException extends BaseException {

    public RefreshTokenMissingException() {
        super(ErrorCode.REFRESH_TOKEN_MISSING);
    }
}
