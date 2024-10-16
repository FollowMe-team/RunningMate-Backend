package com.follow_me.running_mate.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Status {
    READY("신청 중"),
    WAIT("승인 대기 중"),
    COMPLETE("승인 완료"),
    REJECT("승인 거절"),
    ;

    private final String toKorean;
}
