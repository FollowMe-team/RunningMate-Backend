package com.follow_me.running_mate.domain.course.service;

import com.follow_me.running_mate.domain.course.entity.Course;
import com.follow_me.running_mate.domain.course.entity.CourseOption;
import com.follow_me.running_mate.domain.enums.CourseOptionType;
import com.follow_me.running_mate.domain.enums.RunningGoal;
import java.util.List;

public interface CourseOptionService {
    List<CourseOption> saveAll(Course course, List<CourseOptionType> options);
    List<CourseOption> getCourseOptions(Course course);
    List<String> getOptionByRunningGoal(RunningGoal runningGoal);
}
