package com.lightningbid.auth.exception;

import com.lightningbid.common.enums.ErrorCode;
import com.lightningbid.common.exception.BaseException;

public class SignUpTokenExpiredException extends BaseException {

    public SignUpTokenExpiredException() {
        super(ErrorCode.SIGN_UP_TOKEN_EXPIRED);
    }
}
