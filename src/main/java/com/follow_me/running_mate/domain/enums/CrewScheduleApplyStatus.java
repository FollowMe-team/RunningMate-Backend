package com.follow_me.running_mate.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CrewScheduleApplyStatus {
    APPLY("신청"),
    CANCEL("취소"),
    PARTICIPATE("참여"),
    ABSENCE("불참"),
    ;
    private final String toKorean;
}
