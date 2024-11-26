package com.follow_me.running_mate.domain.crew.exception;

import com.follow_me.running_mate.global.error.code.ResultCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CrewErrorCode implements ResultCode {
    NOT_FOUND("CREW001", "크루를 찾을 수 없습니다."),


    ALREADY_EXISTS("CREW002", "이미 신청중입니다."),
    SCHEDULE_CONFLICT("CREW003", "해당 시간에 이미 등록된 일정이 있습니다"),
    FORBIDDEN_ACCESS("CREW004", "해당 사용자에게 권한이 없습니다."),
    SCHEDULE_FULL("CREW005","인원을 초과했습니다." );
    private final String code;
    private final String message;
}
