package com.follow_me.running_mate.domain.course.mapper;

import com.follow_me.running_mate.domain.course.dto.response.CourseResponse;
import com.follow_me.running_mate.domain.course.entity.Course;
import com.follow_me.running_mate.domain.course.entity.CourseOption;
import com.follow_me.running_mate.domain.course.entity.CoursePoint;
import com.follow_me.running_mate.domain.enums.CourseOptionType;
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
}
