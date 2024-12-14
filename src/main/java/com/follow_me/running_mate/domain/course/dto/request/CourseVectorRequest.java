package com.follow_me.running_mate.domain.course.dto.request;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CourseVectorRequest {
    private Long id;
    private String name;
    private String description;
    private double startLatitude;
    private double startLongitude;
    private Double distance;
    private Long duration;
    private String difficulty;
    private List<String> options;
}
