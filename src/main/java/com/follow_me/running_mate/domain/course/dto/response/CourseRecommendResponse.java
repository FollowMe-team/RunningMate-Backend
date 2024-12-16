package com.follow_me.running_mate.domain.course.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CourseRecommendResponse {
    private int statusCode;
    private CourseIds body;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CourseIds {
        private List<Long> courseIds;
    }
}
