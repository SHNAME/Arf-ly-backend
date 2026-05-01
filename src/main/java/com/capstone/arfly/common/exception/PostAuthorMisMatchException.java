package com.capstone.arfly.common.exception;

public class PostAuthorMisMatchException extends BusinessException {
    public PostAuthorMisMatchException(){
        super(ErrorCode.POST_AUTHOR_MISMATCH);
    }
}
