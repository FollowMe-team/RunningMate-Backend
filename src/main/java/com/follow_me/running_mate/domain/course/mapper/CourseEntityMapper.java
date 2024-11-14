package com.follow_me.running_mate.domain.course.mapper;

import com.follow_me.running_mate.domain.course.entity.Course;
import com.follow_me.running_mate.domain.course.entity.CourseBookmark;
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
}
