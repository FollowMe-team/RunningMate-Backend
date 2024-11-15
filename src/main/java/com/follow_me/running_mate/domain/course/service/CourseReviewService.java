package com.follow_me.running_mate.domain.course.service;

import com.follow_me.running_mate.domain.course.dto.request.CourseRequest;
import com.follow_me.running_mate.domain.course.entity.Course;
import com.follow_me.running_mate.domain.course.entity.CourseReview;
import com.follow_me.running_mate.domain.course.entity.CourseReviewImage;
import com.follow_me.running_mate.domain.enums.ReviewSortType;
import com.follow_me.running_mate.domain.member.entity.Member;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface CourseReviewService {

    CourseReview save(Course course, Member member, CourseRequest.CreateReviewRequest request);
    CourseReview delete(Long reviewId, Member member);
    List<CourseReviewImage> saveImages(CourseReview courseReview, List<MultipartFile> images);

    Double getAverageRating(Course course);
    List<CourseReview> getRecentReviews(Course course);
    List<CourseReview> getReviews(Course course, ReviewSortType sortType);
}
