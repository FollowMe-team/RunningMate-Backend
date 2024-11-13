package com.follow_me.running_mate.domain.member.exception;

import com.follow_me.running_mate.global.error.code.ResultCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode implements ResultCode {
    // Member 도메인 에러 코드 (MEMBER001 ~ MEMBER999)
    NOT_FOUND("MEMBER001", "회원을 찾을 수 없습니다."),
    NO_CHANGES_DETECTED("MEMBER002", "변경할 프로필 정보가 없습니다."),
    INCORRECT_CURRENT_PASSWORD("MEMBER003", "현재 비밀번호가 일치하지 않습니다."),
    SAME_AS_CURRENT_PASSWORD("MEMBER004", "새 비밀번호가 현재 비밀번호와 동일합니다."),
    PASSWORDS_DO_NOT_MATCH("MEMBER005", "입력한 비밀번호와 변경할 비밀번호가 일치하지 않습니다."),
    ;

    private final String code;
    private final String message;
}