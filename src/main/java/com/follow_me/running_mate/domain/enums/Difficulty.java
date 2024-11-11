package com.follow_me.running_mate.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Difficulty {
    EASY("쉬움"),
    NORMAL("보통"),
    HARD("어려움"),
    NONE("없음")
    ;
    private final String toKorean;
}
