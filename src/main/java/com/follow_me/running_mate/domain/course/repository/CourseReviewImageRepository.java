package com.follow_me.running_mate.domain.course.repository;

import com.follow_me.running_mate.domain.course.entity.CourseReview;
import com.follow_me.running_mate.domain.course.entity.CourseReviewImage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseReviewImageRepository extends JpaRepository<CourseReviewImage, Long> {
    List<CourseReviewImage> findAllByReview(CourseReview review);
}