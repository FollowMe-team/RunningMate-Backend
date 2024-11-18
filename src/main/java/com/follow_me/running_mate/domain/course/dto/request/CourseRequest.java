package com.follow_me.running_mate.domain.course.dto.request;

import com.follow_me.running_mate.domain.course.validation.annotation.UniqueCourseName;
import com.follow_me.running_mate.domain.enums.CourseOptionType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CourseRequest {

    @Getter
    @AllArgsConstructor
    public static class CreateCourseRequest {

        @NotBlank(message = "코스명을 입력해주세요.")
        @UniqueCourseName
        private String name;

        @NotNull(message = "코스 설명을 입력해주세요.")
        private String description;

        @NotNull(message = "시/도를 선택해주세요.")
        private String city;

        @NotNull(message = "구/군을 선택해주세요.")
        private String district;

        @Min(0)
        private Double distance;

        @NotNull(message = "옵션이 없다면, 빈 배열을 보내주세요.")
        private List<CourseOptionType> options;

        @Size(min = 2, message = "최소 2개의 코스 포인트를 입력해주세요.")
        private List<CoursePointInfo> coursePoints;
    }

    @Getter
    @AllArgsConstructor
    public static class CreateCourseRecordRequest {

        @NotNull(message = "시작 시간을 입력해주세요.")
        private LocalDateTime startTime;

        @NotNull(message = "종료 시간을 입력해주세요.")
        private LocalDateTime endTime;

        @NotNull(message = "거리(km)를 입력해주세요.")
        private Double distance;

        @NotNull(message = "평균 페이스를 입력해주세요.")
        private Integer averagePace;

        @NotNull(message = "소모 칼로리를 입력해주세요.")
        private Integer caloriesBurned;

        @NotNull(message = "기록 포인트를 입력해주세요.")
        private List<RecordPointRequest> recordPoints;
    }

    @Getter
    @AllArgsConstructor
    public static class CreateReviewRequest {
        @NotNull
        private String content;

        @Min(1)
        @Max(5)
        private Integer rating;
    }

    public interface GeoPoint {
        Double getLatitude();
        Double getLongitude();
    }

    @Getter
    @AllArgsConstructor
    public static class CoursePointInfo implements GeoPoint {
        private Double latitude;
        private Double longitude;
    }

    @Getter
    @AllArgsConstructor
    public static class RecordPointRequest implements GeoPoint {
        private Double latitude;
        private Double longitude;
        private LocalDateTime recordedTime;
    }
}
