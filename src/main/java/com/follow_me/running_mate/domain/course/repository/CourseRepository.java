package com.follow_me.running_mate.domain.course.repository;

import com.follow_me.running_mate.domain.course.entity.Course;
import com.follow_me.running_mate.domain.course.exception.CourseErrorCode;
import com.follow_me.running_mate.domain.member.entity.Member;
import com.follow_me.running_mate.global.error.exception.CustomException;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {

    default Course getCourse(Long id) {
        return findById(id)
            .orElseThrow(() -> new CustomException(CourseErrorCode.NOT_FOUND));
    }

    List<Course> findAllByWriterOrderByCreatedAtDesc(Member writer);
}
