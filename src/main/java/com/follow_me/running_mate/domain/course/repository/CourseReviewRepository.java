package com.follow_me.running_mate.domain.course.repository;

import com.follow_me.running_mate.domain.course.entity.Course;
import com.follow_me.running_mate.domain.course.entity.CourseReview;
import com.follow_me.running_mate.domain.course.exception.CourseErrorCode;
import com.follow_me.running_mate.global.error.exception.CustomException;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CourseReviewRepository extends JpaRepository<CourseReview, Long> {

    default CourseReview getCourseReview(Long id) {
        return findById(id)
            .orElseThrow(() -> new CustomException(CourseErrorCode.NOT_FOUND_REVIEW));
    }

    int countAllByCourse(Course course);

    // 특정 Course 객체의 평점 평균을 계산하는 쿼리
    @Query("SELECT COALESCE(AVG(r.rating), 0) FROM CourseReview r WHERE r.course = :course")
    Double findAverageRatingByCourse(@Param("course") Course course);

    // 최신순
    List<CourseReview> findByCourseOrderByCreatedAtDesc(Course course);
    List<CourseReview> findTop3ByCourseOrderByCreatedAtDesc(Course course);

    // 오래된순
    List<CourseReview> findByCourseOrderByCreatedAtAsc(Course course);

    // 높은 평점순
    List<CourseReview> findByCourseOrderByRatingDesc(Course course);

    // 낮은 평점순
    List<CourseReview> findByCourseOrderByRatingAsc(Course course);
}
