package com.follow_me.running_mate.domain.course.exception;

import com.follow_me.running_mate.global.error.code.ResultCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CourseErrorCode implements ResultCode {
    NOT_FOUND("COURSE001", "코스를 찾을 수 없습니다."),
    ;

    private final String code;
    private final String message;
}
