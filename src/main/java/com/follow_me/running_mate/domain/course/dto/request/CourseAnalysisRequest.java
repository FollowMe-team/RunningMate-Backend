package com.follow_me.running_mate.domain.course.dto.request;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CourseAnalysisRequest {
    private Double totalDistance;
    private List<PointInfo> points;

    @Getter
    @Builder
    public static class PointInfo {
        private Double x;
        private Double y;
        private Double elevation;
        private Integer sequenceNumber;
    }
}

