package com.lightningbid.auth.exception;

import com.lightningbid.common.enums.ErrorCode;
import com.lightningbid.common.exception.BaseException;

public class RefreshTokenException extends BaseException {

    public RefreshTokenException() {
        super(ErrorCode.REFRESH_TOKEN_INVALID);
    }
}
