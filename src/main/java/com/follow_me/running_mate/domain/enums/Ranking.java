package com.follow_me.running_mate.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Ranking {
    JOGGER("조깅러", "달리기를 시작한 초보 러너", "평균 속도 4-6km/h, 평균 거리 1-3km (최근 한 달 동안)"),

    RUNNER("러너", "꾸준히 달리기를 즐기는 러너", "평균 속도 6-8km/h, 평균 거리 3-5km (최근 한 달 동안)"),

    RACER("레이서", "속도와 거리를 높여가는 중급 러너", "평균 속도 8-10km/h, 평균 거리 5-8km (최근 한 달 동안)"),

    SPRINTER("스프린터", "빠른 속도로 달리는 전문가", "평균 속도 10-12km/h, 평균 거리 8-12km (최근 한 달 동안)"),

    MARATHONER("마라토너", "장거리 달리기의 마스터", "평균 속도 12-14km/h, 평균 거리 12-20km (최근 한 달 동안)"),

    ULTRA_RUNNER("울트라 러너", "극한의 장거리를 정복하는 러너", "평균 속도 12-15km/h, 평균 거리 20-30km (최근 한 달 동안)"),

    IRON_LEGS("아이언 레그", "어떤 코스도 견디는 강철 다리의 소유자", "평균 속도 14-16km/h, 평균 거리 30-50km (최근 한 달 동안)"),

    SPEED_DEMON("스피드 데몬", "번개같은 속도의 전설적인 러너", "평균 속도 15km/h 이상, 평균 거리 50km 이상 (최근 한 달 동안)");

    private final String name;
    private final String description;
    private final String criteria;
}
