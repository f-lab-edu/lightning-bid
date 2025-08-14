package com.lightningbid.file.exception;

import com.lightningbid.common.enums.ErrorCode;
import com.lightningbid.common.exception.BaseException;

public class FileSaveFailedException extends BaseException {

    public FileSaveFailedException() {
        super(ErrorCode.FILE_SAVE_FAILED);
    }
}
