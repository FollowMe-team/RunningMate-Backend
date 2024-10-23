package com.follow_me.running_mate.domain.member.exception;

import com.follow_me.running_mate.global.error.code.ResultCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode implements ResultCode {
    // Member 도메인 에러 코드 (MEMBER001 ~ MEMBER999)
    NOT_FOUND("MEMBER001", "회원을 찾을 수 없습니다."),
    ;

    private final String code;
    private final String message;
}