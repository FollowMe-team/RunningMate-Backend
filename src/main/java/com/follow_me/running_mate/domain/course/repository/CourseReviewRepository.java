package com.follow_me.running_mate.domain.course.repository;

import com.follow_me.running_mate.domain.course.entity.Course;
import com.follow_me.running_mate.domain.course.entity.CourseReview;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CourseReviewRepository extends JpaRepository<CourseReview, Long> {

    // 특정 Course 객체의 평점 평균을 계산하는 쿼리
    @Query("SELECT COALESCE(AVG(r.rating), 0) FROM CourseReview r WHERE r.course = :course")
    Double findAverageRatingByCourse(@Param("course") Course course);
}
