package com.follow_me.running_mate.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Difficulty {
    WAITING("검토 중"),
    EASY("쉬움"),
    NORMAL("보통"),
    HARD("어려움"),
    ;
    private final String toKorean;
}
