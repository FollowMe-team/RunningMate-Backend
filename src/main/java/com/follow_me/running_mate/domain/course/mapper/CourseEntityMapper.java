package com.follow_me.running_mate.domain.course.mapper;

import com.follow_me.running_mate.domain.course.dto.request.CourseRequest;
import com.follow_me.running_mate.domain.course.entity.Course;
import com.follow_me.running_mate.domain.course.entity.CourseBookmark;
import com.follow_me.running_mate.domain.course.entity.CourseImage;
import com.follow_me.running_mate.domain.course.entity.CourseOption;
import com.follow_me.running_mate.domain.course.entity.CoursePoint;
import com.follow_me.running_mate.domain.course.entity.CourseRecord;
import com.follow_me.running_mate.domain.course.entity.CourseRecordPoint;
import com.follow_me.running_mate.domain.course.entity.CourseReview;
import com.follow_me.running_mate.domain.course.entity.CourseReviewImage;
import com.follow_me.running_mate.domain.enums.CourseImageType;
import com.follow_me.running_mate.domain.enums.CourseOptionType;
import com.follow_me.running_mate.domain.enums.Status;
import com.follow_me.running_mate.domain.member.entity.Member;
import com.follow_me.running_mate.global.common.util.FormatterUtil;
import java.time.Duration;
import org.springframework.stereotype.Component;

@Component
public class CourseEntityMapper {

    public Course toCourse(CourseRequest.CreateCourseRequest request, Member member) {
        return Course.builder()
            .name(request.getName())
            .description(request.getDescription())
            .startPoint(FormatterUtil.formatPoint(request.getCoursePoints().get(0)))
            .endPoint(FormatterUtil.formatPoint(request.getCoursePoints().get(request.getCoursePoints().size() - 1)))
            .path(FormatterUtil.formatLineString(request.getCoursePoints()))
            .distance(request.getDistance())
            .duration(FormatterUtil.formatDuration(request.getDistance()))
            .district(request.getDistrict())
            .city(request.getCity())
            .district(request.getDistrict())
            .status(Status.READY)
            .writer(member)
            .build();
    }

    public CourseImage toCourseImage(Course course, String imageUrl, CourseImageType type) {
        return CourseImage.builder()
            .course(course)
            .url(imageUrl)
            .type(type)
            .build();
    }

    public CourseOption toCourseOption(Course course, CourseOptionType type) {
        return CourseOption.builder()
            .course(course)
            .type(type)
            .build();
    }

    public CoursePoint toCoursePoint(Course course, CourseRequest.CoursePointInfo pointInfo, Integer order) {
        return CoursePoint.builder()
            .course(course)
            .location(FormatterUtil.formatPoint(pointInfo))
            .elevation(pointInfo.getElevation())
            .sequenceNumber(order)
            .build();
    }

    public CourseBookmark toCourseBookmark(Course course, Member member) {
        return CourseBookmark.builder()
            .course(course)
            .member(member)
            .isBookmarked(true)
            .build();
    }

    public CourseReview toCourseReview(Course course, Member member, CourseRequest.CreateReviewRequest request) {
        return CourseReview.builder()
            .course(course)
            .writer(member)
            .content(request.getContent())
            .rating(request.getRating())
            .build();
    }

    public CourseReviewImage toCourseReviewImage(CourseReview courseReview, String imageUrl) {
        return CourseReviewImage.builder()
            .review(courseReview)
            .url(imageUrl)
            .build();
    }

    public CourseRecord toCourseRecord(Course course, Member member, CourseRequest.CreateCourseRecordRequest request) {
        return CourseRecord.builder()
            .course(course)
            .runner(member)
            .startTime(request.getStartTime())
            .endTime(request.getEndTime())
            .duration(Duration.ofSeconds(request.getDuration()))
            .distance(request.getDistance())
            .averagePace(request.getAveragePace())
            .caloriesBurned(request.getCaloriesBurned())
            .path(FormatterUtil.formatLineString(request.getRecordPoints()))
            .build();
    }

    public CourseRecordPoint toCourseRecordPoint(CourseRecord courseRecord, CourseRequest.RecordPointRequest point) {
        return CourseRecordPoint.builder()
            .record(courseRecord)
            .location(FormatterUtil.formatPoint(point))
            .recordedTime(point.getRecordedTime())
            .build();
    }
}
