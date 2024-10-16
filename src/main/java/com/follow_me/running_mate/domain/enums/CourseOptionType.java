package com.follow_me.running_mate.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CourseOptionType {
    FOREST("숲"),
    RIVER("강"),
    LAKE("호수"),
    MOUNTAIN("산"),
    SEA("바다"),
    CITY("도시"),
    PARK("공원"),
    FIELD("들"),
    EXIST_STAIR("계단 존재"),
    EXIST_UPHILL("오르막길 존재"),
    EXIST_DOWNHILL("내리막길 존재"),
    DOG_WALKABLE("강아지 산책 가능"),
    BICYCLE_WALKABLE("자전거 산책 가능"),
    BABY_WALKABLE("유모차 산책 가능"),
    WHEELCHAIR_WALKABLE("휠체어 산책 가능")
    ;

    private final String toKorean;
}
