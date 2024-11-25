package com.follow_me.running_mate.domain.crew.exception;

import com.follow_me.running_mate.global.error.code.ResultCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CrewErrorCode implements ResultCode {
    NOT_FOUND("CREW001", "크루를 찾을 수 없습니다."),


    ALREADY_EXISTS("CREW002","이미 신청중입니다." );
    private final String code;
    private final String message;
}
