package com.follow_me.running_mate.domain.course.repository;

import com.follow_me.running_mate.domain.course.entity.Course;
import com.follow_me.running_mate.domain.course.entity.CourseOption;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseOptionRepository extends JpaRepository<CourseOption, Long> {
    List<CourseOption> findAllByCourse(Course course);
}
