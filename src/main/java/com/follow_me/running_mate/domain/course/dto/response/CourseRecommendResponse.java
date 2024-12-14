package com.follow_me.running_mate.domain.course.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CourseRecommendResponse {
    private List<Long> courseIds;
}
