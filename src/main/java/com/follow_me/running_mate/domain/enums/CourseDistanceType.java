package com.follow_me.running_mate.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CourseDistanceType {

    UNDER_3KM("3km 미만"),
    BETWEEN_3KM_AND_5KM("3km ~ 5km"),
    BETWEEN_5KM_AND_10KM("5km ~ 10km"),
    UPPER_10KM("10km 이상");

    private final String toKorean;
}

