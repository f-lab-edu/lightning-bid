package com.lightningbid.file.exception;

import com.lightningbid.common.enums.ErrorCode;
import com.lightningbid.common.exception.BaseException;

public class FileNotFoundException extends BaseException {

    public FileNotFoundException() {
        super(ErrorCode.FILE_NOT_FOUND);
    }
}
