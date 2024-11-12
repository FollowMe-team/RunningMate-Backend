package com.follow_me.running_mate.domain.course.mapper;

import com.follow_me.running_mate.domain.course.dto.response.CourseResponse;
import com.follow_me.running_mate.domain.course.entity.Course;
import com.follow_me.running_mate.domain.course.entity.CourseOption;
import com.follow_me.running_mate.domain.course.entity.CoursePoint;
import com.follow_me.running_mate.domain.course.entity.CourseReview;
import com.follow_me.running_mate.domain.enums.CourseOptionType;
import com.follow_me.running_mate.domain.member.entity.Member;
import com.follow_me.running_mate.global.common.util.FormatterUtil;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class CourseMapper {

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

    public CourseResponse.ReviewInfo toReviewInfo(
        CourseReview review, List<String> reviewImages,  Member member
    ) {
        return CourseResponse.ReviewInfo.builder()
            .id(review.getId())
            .writer(toMemberInfo(review.getWriter()))
            .content(review.getContent())
            .rating(review.getRating())
            .images(reviewImages)
            .createdAt(FormatterUtil.formatTime(review.getCreatedAt()))
            .isMine(review.getWriter().equals(member)) // TODO: 되는지 테스트
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
}
