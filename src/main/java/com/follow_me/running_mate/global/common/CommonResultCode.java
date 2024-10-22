package com.follow_me.running_mate.global.common;

import com.follow_me.running_mate.global.error.code.ResultCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommonResultCode implements ResultCode {
    SUCCESS("200", "성공적으로 처리되었습니다.")
    ;

    private final String code;
    private final String message;
}
