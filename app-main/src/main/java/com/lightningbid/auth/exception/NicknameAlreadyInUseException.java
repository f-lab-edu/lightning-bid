package com.lightningbid.auth.exception;

import com.lightningbid.common.enums.ErrorCode;
import com.lightningbid.common.exception.BaseException;

public class NicknameAlreadyInUseException extends BaseException {

    public NicknameAlreadyInUseException() {
        super(ErrorCode.NICKNAME_DUPLICATE);
    }

    public NicknameAlreadyInUseException(String message) {
        super(message, ErrorCode.NICKNAME_DUPLICATE);
    }
}
