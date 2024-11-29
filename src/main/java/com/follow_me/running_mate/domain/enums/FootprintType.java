package com.follow_me.running_mate.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FootprintType {
    GOOD("좋았어요"),
    NORMAL("보통이었어요"),
    BAD("별로였어요"),
    ;
    private final String toKorean;
}
