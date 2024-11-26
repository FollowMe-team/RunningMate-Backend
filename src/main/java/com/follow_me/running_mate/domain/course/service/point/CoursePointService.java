package com.follow_me.running_mate.domain.course.service.point;

import com.follow_me.running_mate.domain.course.dto.request.CourseRequest;
import com.follow_me.running_mate.domain.course.entity.Course;
import com.follow_me.running_mate.domain.course.entity.CoursePoint;
import java.util.List;

public interface CoursePointService {

    void saveCoursePoint(CoursePoint coursePoint);
    List<CoursePoint> saveCoursePoints(Course course, List<CourseRequest.CoursePointInfo> coursePoints);
    List<CoursePoint> getCoursePoints(Course course);
    List<CoursePoint> getCoursePointsByCourseId(Long courseId);
}
