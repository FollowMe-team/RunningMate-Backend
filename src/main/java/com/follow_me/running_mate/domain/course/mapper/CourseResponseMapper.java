package com.follow_me.running_mate.domain.course.mapper;

import com.follow_me.running_mate.domain.course.dto.response.CourseResponse;
import com.follow_me.running_mate.domain.course.entity.*;
import com.follow_me.running_mate.domain.crew.entity.Crew;
import com.follow_me.running_mate.domain.enums.CourseOptionType;
import com.follow_me.running_mate.domain.member.entity.Member;
import com.follow_me.running_mate.global.common.util.FormatterUtil;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class CourseResponseMapper {

    public CourseResponse.SummaryInfo toSummaryInfo(
        Course course,
        Double rating,
        Integer runningCount,
        Boolean isBookmarked,
        List<CourseOption> courseOptions,
        List<CoursePoint> coursePointInfos
    ) {
        return CourseResponse.SummaryInfo.builder()
            .id(course.getId())
            .name(course.getName())
            .description(course.getDescription())
            .location(FormatterUtil.formatLocation(course.getCity(), course.getDistrict()))
            .distance(course.getDistance())
            .duration(FormatterUtil.formatDuration(course.getDuration()))
            .difficulty(course.getDifficulty())
            .rating(FormatterUtil.formatRating(rating))
            .runningCount(runningCount)
            .isBookmarked(isBookmarked)
            .courseOptionTypes(toCourseOptionTypes(courseOptions))
            .coursePointInfos(toCoursePointInfos(coursePointInfos))
            .build();
    }

    public CourseResponse.MyCourseInfo toMyCourseInfo(
        Course course,
        Double rating,
        Integer runningCount,
        Boolean isBookmarked,
        List<CourseOption> courseOptions,
        List<CoursePoint> coursePointInfos
    ) {
        return CourseResponse.MyCourseInfo.builder()
            .id(course.getId())
            .name(course.getName())
            .description(course.getDescription())
            .location(FormatterUtil.formatLocation(course.getCity(), course.getDistrict()))
            .distance(course.getDistance())
            .duration(FormatterUtil.formatDuration(course.getDuration()))
            .difficulty(course.getDifficulty())
            .status(course.getStatus())
            .rating(FormatterUtil.formatRating(rating))
            .runningCount(runningCount)
            .isBookmarked(isBookmarked)
            .courseOptionTypes(toCourseOptionTypes(courseOptions))
            .coursePointInfos(toCoursePointInfos(coursePointInfos))
            .build();
    }

    public CourseResponse.CourseDetailResponse toCourseDetailResponse(
        Course course, Double rating, Boolean isBookmarked, List<CourseImage> images,
        List<CourseOption> courseOptions, List<CoursePoint> coursePoints,
        List<CourseResponse.CrewInfo> crews, List<CourseResponse.ReviewInfo> reviews
    ) {
        return CourseResponse.CourseDetailResponse.builder()
            .id(course.getId())
            .name(course.getName())
            .description(course.getDescription())
            .location(FormatterUtil.formatLocation(course.getCity(), course.getDistrict()))
            .distance(course.getDistance())
            .duration(FormatterUtil.formatDuration(course.getDuration()))
            .difficulty(course.getDifficulty())
            .rating(FormatterUtil.formatRating(rating))
            .runningCount(course.getRunningCount())
            .isBookmarked(isBookmarked)
            .courseOptionTypes(toCourseOptionTypes(courseOptions))
            .coursePointInfos(toCoursePointInfos(coursePoints))
            .images(images.stream().map(this::toCourseImageInfo).toList())
            .crews(crews)
            .crewCount(crews.size())
            .reviews(reviews)
            .reviewCount(reviews.size())
            .build();
    }

    public CourseResponse.CoursePointDetail toCoursePointDetail(CoursePoint coursePoint) {
        return CourseResponse.CoursePointDetail.builder()
            .latitude(coursePoint.getLocation().getY())
            .longitude(coursePoint.getLocation().getX())
            .voice(coursePoint.getVoice())
            .build();
    }

    public CourseResponse.CourseReviewListResponse toCourseReviewListResponse(
        List<CourseResponse.ReviewInfo> reviews, Double rating, List<Integer> ratingCounts
    ) {
        return CourseResponse.CourseReviewListResponse.builder()
            .rating(rating)
            .ratingCounts(ratingCounts)
            .reviews(reviews)
            .reviewCount(reviews.size())
            .build();
    }

    public CourseResponse.CourseImageInfo toCourseImageInfo(CourseImage courseImage) {
        return CourseResponse.CourseImageInfo.builder()
            .id(courseImage.getId())
            .imageUrl(courseImage.getUrl())
            .type(courseImage.getType())
            .build();
    }

    public List<CourseResponse.ReviewInfo> toReviewInfos(
        List<CourseReview> reviews, Member member
    ) {
        return reviews.stream()
            .map(review -> toReviewInfo(review, member))
            .toList();
    }

    public List<CourseResponse.CrewInfo> toCrewInfos(List<Crew> crews) {
        return crews.stream()
            .map(this::toCrewInfo)
            .toList();
    }

    private CourseResponse.ReviewInfo toReviewInfo(
        CourseReview review,  Member member
    ) {
        return CourseResponse.ReviewInfo.builder()
            .id(review.getId())
            .writer(toMemberInfo(review.getWriter()))
            .content(review.getContent())
            .rating(review.getRating())
            .images(
                review.getImages().stream()
                    .map(this::toReviewImageInfo)
                    .toList()
            )
            .createdAt(FormatterUtil.formatTime(review.getCreatedAt()))
            .isMine(review.getWriter().getId().equals(member.getId()))
            .build();
    }

    private CourseResponse.ReviewImageInfo toReviewImageInfo(CourseReviewImage reviewImage) {
        return CourseResponse.ReviewImageInfo.builder()
            .id(reviewImage.getId())
            .imageUrl(reviewImage.getUrl())
            .build();
    }

    private CourseResponse.CrewInfo toCrewInfo(Crew crew) {
        return CourseResponse.CrewInfo.builder()
            .id(crew.getId())
            .name(crew.getName())
            .shortDescription(crew.getShortDescription())
            .profileImageUrl(crew.getProfileImageUrl())
            .memberCount(crew.getMemberCount())
            .build();
    }

    private List<CourseOptionType> toCourseOptionTypes(List<CourseOption> options) {
        return options.stream()
            .map(CourseOption::getType)
            .toList();
    }

    private List<CourseResponse.CoursePointInfo> toCoursePointInfos(List<CoursePoint> coursePoints) {
        return coursePoints.stream()
            .map(coursePoint ->
                CourseResponse.CoursePointInfo.builder()
                    .latitude(coursePoint.getLocation().getY())
                    .longitude(coursePoint.getLocation().getX())
                    .build()
            ).toList();
    }

    private CourseResponse.MemberInfo toMemberInfo(Member member) {
        return CourseResponse.MemberInfo.builder()
            .id(member.getId())
            .nickname(member.getNickname())
            .profileImageUrl(member.getProfileImageUrl())
            .ranking(member.getRanking())
            .build();
    }
    public CourseResponse.CourseRecordInfo toCourseRecordInfo(CourseRecord courseRecord, Boolean isMine) {
        return CourseResponse.CourseRecordInfo.builder()
            .recordId(courseRecord.getId())
            .startTime(courseRecord.getStartTime())
            .course(toCourseInfo(courseRecord.getCourse(), isMine))
            .distance(courseRecord.getDistance())
            .caloriesBurned(courseRecord.getCaloriesBurned())
            .duration(FormatterUtil.formatDuration(courseRecord.getStartTime(), courseRecord.getEndTime()))
            .averagePace(courseRecord.getAveragePace())
            .build();
    }

    private CourseResponse.CourseInfo toCourseInfo(Course course, Boolean isMine) {
        if (isMine) {
            return CourseResponse.CourseInfo.builder()
                .courseId(course.getId())
                .courseName(course.getName())
                .build();
        }
        return null;
    }
}
