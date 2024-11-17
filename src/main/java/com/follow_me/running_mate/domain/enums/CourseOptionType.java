package com.follow_me.running_mate.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CourseOptionType {
    FOREST("숲길"),
    RIVERSIDE("강변"),
    LAKESIDE("호숫가"),
    MOUNTAIN("산길"),
    SEASIDE("해변"),
    CITYSCAPE("도심"),
    PARK("공원"),
    TRAIL("트레일"),
    CAMPUS("캠퍼스"),
    TRACK("트랙"),
    GRADIENT_HIGH("경사가 심함"),
    GRADIENT_MIDDLE("경사가 중간"),
    GRADIENT_LOW("경사가 약함"),
    GRADIENT_NONE("평지"),
    DOG_WALKABLE("강아지 산책 가능"),
    BICYCLE_WALKABLE("자전거 산책 가능"),
    BABY_WALKABLE("유모차 산책 가능")
    ;

    private final String toKorean;
}
