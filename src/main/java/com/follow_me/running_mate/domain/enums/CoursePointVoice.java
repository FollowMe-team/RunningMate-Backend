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
    NONE("없음")
    ;
    private final String toKorean;
}
