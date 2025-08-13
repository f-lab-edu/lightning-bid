package com.lightningbid.file.exception;

import com.lightningbid.common.enums.ErrorCode;
import com.lightningbid.common.exception.BaseException;

public class FileNotImageException extends BaseException {

    public FileNotImageException() {
        super(ErrorCode.FILE_NOT_IMAGE);
    }
}
