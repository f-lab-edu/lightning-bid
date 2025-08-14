package com.lightningbid.auth.exception;

import com.lightningbid.common.enums.ErrorCode;
import com.lightningbid.common.exception.BaseException;

public class UserAlreadyRegisteredException extends BaseException {

    public UserAlreadyRegisteredException() {
        super(ErrorCode.USER_ALREADY_REGISTERED);
    }
}
