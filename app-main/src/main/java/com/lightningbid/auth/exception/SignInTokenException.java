package com.lightningbid.auth.exception;

import com.lightningbid.common.enums.ErrorCode;
import com.lightningbid.common.exception.BaseException;

public class SignInTokenException extends BaseException {

    public SignInTokenException() {
        super(ErrorCode.SIGN_IN_TOKEN_INVALID);
    }
}
