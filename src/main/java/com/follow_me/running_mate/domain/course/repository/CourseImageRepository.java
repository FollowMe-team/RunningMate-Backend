package com.follow_me.running_mate.domain.course.repository;

import com.follow_me.running_mate.domain.course.entity.Course;
import com.follow_me.running_mate.domain.course.entity.CourseImage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseImageRepository extends JpaRepository<CourseImage, Long> {
    List<CourseImage> findAllByCourse(Course course);
}
