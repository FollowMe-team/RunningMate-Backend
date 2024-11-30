package com.follow_me.running_mate.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CoursePointVoice {
    STRAIGHT("직진"),
    LEFT("좌회전"),
    RIGHT("우회전"),
    U_TURN("U턴"),
    DOWNHILL("내리막"),
    UP_HILL("오르막"),
    STEEP_UPHILL("급경사 오르막"),   // 15% 이상 경사
    STEEP_DOWNHILL("급경사 내리막"), // -15% 이상 경사
    NONE("없음")
    ;
    private final String toKorean;
}
