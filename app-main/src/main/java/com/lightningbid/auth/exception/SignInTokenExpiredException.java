package com.lightningbid.auth.exception;

import com.lightningbid.common.enums.ErrorCode;
import com.lightningbid.common.exception.BaseException;

public class SignInTokenExpiredException extends BaseException {

    public SignInTokenExpiredException() {
        super(ErrorCode.SIGN_IN_TOKEN_EXPIRED);
    }
}
