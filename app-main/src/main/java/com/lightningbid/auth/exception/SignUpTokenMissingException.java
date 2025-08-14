package com.lightningbid.auth.exception;

import com.lightningbid.common.enums.ErrorCode;
import com.lightningbid.common.exception.BaseException;

public class SignUpTokenMissingException extends BaseException {

    public SignUpTokenMissingException() {
        super(ErrorCode.SIGN_UP_TOKEN_MISSING);
    }
}
