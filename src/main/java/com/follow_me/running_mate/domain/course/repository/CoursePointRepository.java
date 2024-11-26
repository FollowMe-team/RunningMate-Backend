package com.follow_me.running_mate.domain.course.repository;

import com.follow_me.running_mate.domain.course.entity.Course;
import com.follow_me.running_mate.domain.course.entity.CoursePoint;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoursePointRepository extends JpaRepository<CoursePoint, Long> {
    List<CoursePoint> findAllByCourseOrderBySequenceNumberAsc(Course course);
    List<CoursePoint> findAllByCourseIdOrderBySequenceNumberAsc(Long courseId);
}
