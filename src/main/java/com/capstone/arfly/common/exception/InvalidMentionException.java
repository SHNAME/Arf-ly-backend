package com.capstone.arfly.common.exception;

public class InvalidMentionException extends BusinessException {
    public InvalidMentionException() {
        super(ErrorCode.INVALID_MENTION);
    }
}
