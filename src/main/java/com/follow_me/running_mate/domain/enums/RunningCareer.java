package com.follow_me.running_mate.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RunningCareer {
    BEGINNER("시작"),
    INTERMEDIATE("1 ~ 3년"),
    ADVANCED("3 ~ 5년"),
    EXPERT("5년 이상")
    ;

    private final String toKorean;
}
