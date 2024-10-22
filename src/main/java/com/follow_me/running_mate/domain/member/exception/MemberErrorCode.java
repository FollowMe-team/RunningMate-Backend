package com.follow_me.running_mate.domain.member.exception;

import com.follow_me.running_mate.global.error.code.ResultCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode implements ResultCode {
    // Member 도메인 에러 코드 (MEMBER001 ~ MEMBER999)
    NOT_FOUND("MEMBER001", "회원을 찾을 수 없습니다."),
    DUPLICATE_EMAIL("MEMBER002", "이미 존재하는 이메일입니다."),
    INVALID_EMAIL_FORMAT("MEMBER003", "잘못된 이메일 형식입니다."),
    INVALID_PASSWORD_FORMAT("MEMBER004", "비밀번호는 8자 이상이어야 합니다.");

    private final String code;
    private final String message;
}