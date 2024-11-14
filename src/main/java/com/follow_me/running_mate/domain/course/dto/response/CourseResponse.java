package com.follow_me.running_mate.domain.course.dto.response;

import com.follow_me.running_mate.domain.enums.CourseOptionType;
import com.follow_me.running_mate.domain.enums.CoursePointVoice;
import com.follow_me.running_mate.domain.enums.Difficulty;
import com.follow_me.running_mate.domain.enums.Ranking;
import com.follow_me.running_mate.domain.enums.Status;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class CourseResponse {

    @Getter
    @AllArgsConstructor
    public static class CreateReviewResponse {
        private Long reviewId;
    }

    @Getter
    @AllArgsConstructor
    public static class CourseListResponse {
        private List<SummaryInfo> courses;
    }

    @Getter
    @AllArgsConstructor
    public static class MyCourseListResponse {
        private List<MyCourseInfo> courses;
    }

    @Getter
    @AllArgsConstructor
    public static class CoursePathResponse {
        private List<CoursePointDetail> coursePoints;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SummaryInfo {
        private Long id;
        private String name;
        private String description;
        private String location;
        private Double distance;
        private String duration;
        private Difficulty difficulty;
        private Double rating;
        private Integer runningCount;
        private boolean isBookmarked;
        private List<CourseOptionType> courseOptionTypes;
        private List<CoursePointInfo> coursePointInfos;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MyCourseInfo {
        private Long id;
        private String name;
        private String description;
        private String location;
        private Double distance;
        private String duration;
        private Difficulty difficulty;
        private Status status;
        private Double rating;
        private Integer runningCount;
        private boolean isBookmarked;
        private List<CourseOptionType> courseOptionTypes;
        private List<CoursePointInfo> coursePointInfos;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CourseDetailResponse {
        private Long id;
        private String name;
        private String description;
        private String location;
        private Double distance;
        private String duration;
        private Difficulty difficulty;
        private Double rating;
        private Integer runningCount;
        private boolean isBookmarked;
        private List<CourseOptionType> courseOptionTypes;
        private List<CoursePointInfo> coursePointInfos;
        private List<String> images;
        private List<CrewInfo> crews;
        private Integer crewCount;
        private List<ReviewInfo> reviews;
        private Integer reviewCount;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CoursePointInfo {
        private double latitude;
        private double longitude;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CoursePointDetail {
        private double latitude;
        private double longitude;
        private CoursePointVoice voice;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CrewInfo {
        private Long id;
        private String name;
        private String shortDescription;
        private String profileImageUrl;
        private Integer memberCount;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CourseReviewListResponse {
        private Double rating;
        private List<Integer> ratingCounts;
        private List<ReviewInfo> reviews;
        private Integer reviewCount;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ReviewInfo {
        private Long id;
        private MemberInfo writer;
        private String content;
        private Integer rating;
        private List<String> images;
        private String createdAt;
        private Boolean isMine;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MemberInfo {
        private Long id;
        private String nickname;
        private String profileImageUrl;
        private Ranking ranking;
        // 러닝 발자국 추가
    }
}
