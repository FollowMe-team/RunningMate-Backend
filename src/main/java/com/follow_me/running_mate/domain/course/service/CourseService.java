package com.follow_me.running_mate.domain.course.service;

import com.follow_me.running_mate.domain.course.dto.request.CourseRequest;
import com.follow_me.running_mate.domain.course.dto.response.CourseResponse;
import com.follow_me.running_mate.domain.enums.CourseOptionType;
import com.follow_me.running_mate.domain.enums.Difficulty;
import com.follow_me.running_mate.domain.enums.ReviewSortType;
import com.follow_me.running_mate.domain.enums.RunningGoal;
import com.follow_me.running_mate.domain.member.entity.Member;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface CourseService {
    CourseResponse.CreateCourseResponse createCourse(
        Member member, CourseRequest.CreateCourseRequest request,
        MultipartFile representativeImage, MultipartFile startImage, MultipartFile endImage
    );
    void bookmarkCourse(Member member, Long courseId);
    void bookmarkCancelCourse(Member member, Long courseId);
    CourseResponse.CreateReviewResponse createCourseReview(
        Member member, Long courseId, CourseRequest.CreateReviewRequest request, List<MultipartFile> images);

    CourseResponse.CourseListResponse getRecentCourses(Member member);
    CourseResponse.CourseListResponse getBookmarkedCourses(Member member);
    CourseResponse.MyCourseListResponse getMyCourses(Member member);
    CourseResponse.CourseListResponse recommendedCourses(
        Member member, Double latitude, Double longitude, Difficulty difficulty, RunningGoal runningGoal
    );
    CourseResponse.CourseListResponse searchCourses(
        Member member, String keyword, Double latitude,
        Double longitude, List<Difficulty> difficulties, List<CourseOptionType> options
    );
    CourseResponse.CourseDetailResponse getCourseDetail(Member member, Long courseId);
    CourseResponse.CourseReviewListResponse getCourseReviews(Member member, Long courseId, ReviewSortType sortType);
    CourseResponse.CoursePathResponse getCoursePath(Long courseId);
}
