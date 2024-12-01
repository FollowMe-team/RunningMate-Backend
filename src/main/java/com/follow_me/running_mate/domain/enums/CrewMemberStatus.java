package com.follow_me.running_mate.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CrewMemberStatus {
    READY("신청 중"),
    COMPLETE("가입 완료"),
    REJECT("가입 거절"),
    OUT("추방")
    ;

    private final String toKorean;
}
