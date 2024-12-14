package com.follow_me.running_mate.domain.course.dto.request;

import com.follow_me.running_mate.domain.enums.Difficulty;
import com.follow_me.running_mate.domain.enums.Ranking;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CourseRecommendRequest {
    private Double latitude;
    private Double longitude;
    private Difficulty difficulty;
    private String goal;
    private Ranking rank;
}