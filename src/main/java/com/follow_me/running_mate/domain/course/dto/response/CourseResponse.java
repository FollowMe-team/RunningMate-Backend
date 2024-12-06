package com.follow_me.running_mate.domain.course.dto.response;

import com.follow_me.running_mate.domain.enums.CourseImageType;
import com.follow_me.running_mate.domain.enums.CourseOptionType;
import com.follow_me.running_mate.domain.enums.CoursePointVoice;
import com.follow_me.running_mate.domain.enums.Difficulty;
import com.follow_me.running_mate.domain.enums.Ranking;
import com.follow_me.running_mate.domain.enums.Status;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class CourseResponse {

    @Getter
    @AllArgsConstructor
    public static class CourseIdResponse {
        private Long courseId;
    }

    @Getter
    @AllArgsConstructor
    public static class CourseRecordIdResponse {
        private Long recordId;
    }

    @Getter
    @AllArgsConstructor
    public static class ReviewIdResponse {
        private Long reviewId;
    }

    @Getter
    @AllArgsConstructor
    public static class CheckCourseNameResponse {
        private boolean isAvailable;
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
    public static class SummaryInfo { //코스 정보 조회시
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
    public static class MyCourseInfo { // 내가 등록한 코스 정보
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
        private List<CourseImageInfo> images;
        private List<CrewInfo> crews;
        private Integer crewCount;
        private List<ReviewInfo> reviews;
        private Integer reviewCount;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CourseImageInfo {
        private Long id;
        private String imageUrl;
        private CourseImageType type;
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
        private List<ReviewImageInfo> images;
        private String createdAt;
        private Boolean isMine;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ReviewImageInfo {
        private Long id;
        private String imageUrl;
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
        private Long footPrint;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CourseRecordInfo {
        private Long recordId;
        private LocalDateTime startTime;
        private CourseInfo course;
        private Double distance;
        private String duration;
        private Integer caloriesBurned;
        private Double averagePace;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CourseInfo {
        private Long courseId;
        private String courseName;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class CourseRecordInfoList {
        private List<CourseRecordInfo> records;
    }
}
