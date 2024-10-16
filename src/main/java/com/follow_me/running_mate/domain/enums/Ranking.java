package com.follow_me.running_mate.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Ranking {

    JOGGER("조깅러", "달리기를 시작한 초보 러너", "",
        "5회 러닝 평균: 속도 6km/h 미만 또는 거리 2km 미만"),

    RUNNER("러너", "꾸준히 달리기를 즐기는 러너", "",
        "5회 러닝 평균: 속도 6-8km/h 또는 거리 2-4km"),

    RACER("레이서", "속도와 거리를 높여가는 중급 러너", "",
        "5회 러닝 평균: 속도 8-10km/h 또는 거리 4-6km"),

    SPRINTER("스프린터", "빠른 속도로 달리는 전문가", "",
        "5회 러닝 평균: 속도 10-12km/h 또는 1회 5km 완주"),

    MARATHONER("마라토너", "장거리 달리기의 마스터", "",
        "5회 러닝 중 1회 이상 10km 이상 완주"),

    ULTRA_RUNNER("울트라 러너", "극한의 장거리를 정복하는 러너", "",
        "5회 러닝 중 1회 이상 반마라톤(21.1km) 완주"),

    IRON_LEGS("아이언 레그", "어떤 코스도 견디는 강철 다리의 소유자", "",
        "5회 러닝 평균: 거리 15km 이상 또는 1회 마라톤 완주"),

    SPEED_DEMON("스피드 데몬", "번개같은 속도의 전설적인 러너", "",
        "5회 러닝 평균: 속도 15km/h 이상 또는 5km 20분 이내 완주");
    ;

    private final String name;
    private final String description;
    private final String iconUrl;
    private final String criteria;
}
