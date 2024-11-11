package com.follow_me.running_mate.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReviewSortType {
    LATEST("최신순"),
    OLDEST("오래된순"),
    HIGHEST_RATING("높은 평점순"),
    LOWEST_RATING("낮은 평점순");

    private final String description;
}
