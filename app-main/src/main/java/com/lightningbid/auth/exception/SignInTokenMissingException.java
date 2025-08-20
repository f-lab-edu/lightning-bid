package com.lightningbid.auth.exception;

import com.lightningbid.common.enums.ErrorCode;
import com.lightningbid.common.exception.BaseException;

public class SignInTokenMissingException extends BaseException {

    public SignInTokenMissingException() {
        super(ErrorCode.SIGN_IN_TOKEN_MISSING);
    }
}
