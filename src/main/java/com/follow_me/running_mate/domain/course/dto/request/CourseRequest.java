package com.follow_me.running_mate.domain.course.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CourseRequest {

    @Getter
    @AllArgsConstructor
    public static class CreateReviewRequest {
        @NotNull
        private String content;

        @Min(1)
        @Max(5)
        private Integer rating;
    }
}
