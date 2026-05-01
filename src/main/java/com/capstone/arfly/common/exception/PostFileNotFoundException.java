package com.capstone.arfly.common.exception;

public class PostFileNotFoundException extends BusinessException {
    public PostFileNotFoundException() {
        super(ErrorCode.POST_FILE_NOT_FOUND);
    }
}
