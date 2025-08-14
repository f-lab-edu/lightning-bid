package com.lightningbid.file.exception;

import com.lightningbid.common.enums.ErrorCode;
import com.lightningbid.common.exception.BaseException;

public class FileEmptyException extends BaseException {

    public FileEmptyException() {
        super(ErrorCode.DEPOSIT_REQUIRED);
    }
}
