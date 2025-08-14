package com.lightningbid.auth.exception;

import com.lightningbid.common.enums.ErrorCode;
import com.lightningbid.common.exception.BaseException;

public class SignUpTokenException extends BaseException {

    public SignUpTokenException() {
        super(ErrorCode.SIGN_UP_TOKEN_INVALID);
    }
}
