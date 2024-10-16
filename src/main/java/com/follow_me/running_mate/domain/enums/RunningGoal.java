package com.follow_me.running_mate.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RunningGoal {
    // TODO: 러닝 목표 카테고리 정하기
    WEIGHT_LOSS("체중 감량"),
    ENDURANCE("체력 향상"),
    SPEED("속도 향상"),
    NONE("없음");

    private final String toKorean;
}
