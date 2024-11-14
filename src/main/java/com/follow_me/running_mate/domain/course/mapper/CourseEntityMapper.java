package com.follow_me.running_mate.domain.course.mapper;

import com.follow_me.running_mate.domain.course.dto.request.CourseRequest;
import com.follow_me.running_mate.domain.course.entity.Course;
import com.follow_me.running_mate.domain.course.entity.CourseBookmark;
import com.follow_me.running_mate.domain.course.entity.CourseReview;
import com.follow_me.running_mate.domain.course.entity.CourseReviewImage;
import com.follow_me.running_mate.domain.member.entity.Member;
import org.springframework.stereotype.Component;

@Component
public class CourseEntityMapper {

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
}
