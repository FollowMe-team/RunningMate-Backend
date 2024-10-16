package com.follow_me.running_mate.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CourseImageType {
    START("출발지"),
    FINISH("도착지"),
    REST("휴식지"),
    REPRESENTATIVE("대표 이미지"),
    ;

    private final String toKorean;
}
