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
    INVALID_PROFILE_API("MEMBER006", "본인 프로필은 마이 프로필 조회 API를 통해 확인해주세요."),
    INVALID_COURSE_RECORD_API("MEMBER007", "본인 코스 기록은 마이 코스 기록 조회 API를 통해 확인해주세요."),
    NOT_FOUND_TARGET("MEMBER008", "존재하지 않는 대상 사용자입니다."),
    NOT_SELF_TARGET("MEMBER009", "본인을 대상으로 할 수 없습니다."),
    ALREADY_FOLLOWING("MEMBER010", "이미 팔로우 중인 사용자입니다."),
    NOT_FOLLOWING("MEMBER011", "팔로우 중이 아닌 사용자입니다."),
    ;

    private final String code;
    private final String message;
}