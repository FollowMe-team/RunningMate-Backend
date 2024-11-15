package com.follow_me.running_mate.domain.course.service.record;

import com.follow_me.running_mate.domain.course.dto.request.CourseRequest;
import com.follow_me.running_mate.domain.course.dto.response.CourseResponse;
import com.follow_me.running_mate.domain.course.entity.Course;
import com.follow_me.running_mate.domain.member.entity.Member;
import java.util.List;

public interface CourseRecordService {

    List<Course> getRecentCourses(Member member);
    CourseResponse.CourseRecordIdResponse createCourseRecord(
        Member member, Course course, CourseRequest.CreateCourseRecordRequest request
    );
}
