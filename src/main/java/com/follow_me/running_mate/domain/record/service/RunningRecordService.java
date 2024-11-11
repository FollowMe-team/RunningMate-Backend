package com.follow_me.running_mate.domain.record.service;

import com.follow_me.running_mate.domain.course.entity.Course;
import com.follow_me.running_mate.domain.member.entity.Member;
import java.util.List;

public interface RunningRecordService {

    List<Course> getRecentCourses(Member member);
}
