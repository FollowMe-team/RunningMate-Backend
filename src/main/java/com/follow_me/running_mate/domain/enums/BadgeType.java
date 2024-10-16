package com.follow_me.running_mate.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BadgeType {

    FIRST_RUN("첫 달리기", "첫 달리기를 완료하셨습니다.", "", "러닝메이트를 통해 첫 달리기를 완료한 경우"),
    COMPLETE_50TIME("50회", "50회 달리기를 완료하셨습니다.", "", "러닝메이트를 통해 50회 달리기를 완료한 경우"),
    COMPLETE_100TIME("100회", "100회 달리기를 완료하셨습니다.", "", "러닝메이트를 통해 100회 달리기를 완료한 경우"),
    COMPLETE_500TIME("500회", "500회 달리기를 완료하셨습니다.", "", "러닝메이트를 통해 500회 달리기를 완료한 경우"),
    COMPLETE_1000TIME("1000회", "1000회 달리기를 완료하셨습니다.", "", "러닝메이트를 통해 1000회 달리기를 완료한 경우"),
    COMPLETE_100KM("100 KM", "100KM 달리기를 완료하셨습니다.", "", "러닝메이트를 통해 100KM를 완주한 경우"),
    COMPLETE_500KM("500 KM", "500KM 달리기를 완료하셨습니다.", "", "러닝메이트를 통해 500KM를 완주한 경우"),
    COMPLETE_1000KM("1000 KM", "1000KM 달리기를 완료하셨습니다.", "", "러닝메이트를 통해 1000KM를 완주한 경우"),
    COMPLETE_5000KM("5000 KM", "5000KM 달리기를 완료하셨습니다.", "", "러닝메이트를 통해 5000KM를 완주한 경우"),
    ;

    private final String name;
    private final String description;
    private final String iconUrl;
    private final String criteria;
}
