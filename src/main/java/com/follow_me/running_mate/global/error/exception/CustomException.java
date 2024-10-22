package com.follow_me.running_mate.global.error.exception;

import com.follow_me.running_mate.global.error.code.ResultCode;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final ResultCode resultCode;

    public CustomException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.resultCode = resultCode;
    }

    public CustomException(ResultCode resultCode, String message) {
        super(message);
        this.resultCode = resultCode;
    }
}
