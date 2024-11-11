package com.follow_me.running_mate.domain.course.service;

import com.follow_me.running_mate.domain.course.dto.response.CourseResponse;
import com.follow_me.running_mate.domain.course.repository.CourseRepository;
import com.follow_me.running_mate.domain.enums.CourseOptionType;
import com.follow_me.running_mate.domain.enums.Difficulty;
import com.follow_me.running_mate.domain.enums.ReviewSortType;
import com.follow_me.running_mate.domain.enums.RunningGoal;
import com.follow_me.running_mate.domain.member.entity.Member;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;

    @Override
    public CourseResponse.CourseListResponse getRecentCourses(Member member) {
        return null;
    }

    @Override
    public CourseResponse.CourseListResponse getBookmarkedCourses(Member member) {
        return null;
    }

    @Override
    public CourseResponse.MyCourseListResponse getMyCourses(Member member) {
        return null;
    }

    @Override
    public CourseResponse.CourseListResponse recommendedCourses(
        Member member, Double latitude, Double longitude, Difficulty difficulty, RunningGoal runningGoal
    ) {
        return null;
    }

    @Override
    public CourseResponse.CourseListResponse searchCourses(String keyword, Double latitude, Double longitude, Difficulty difficulty, List<CourseOptionType> options) {
        return null;
    }

    @Override
    public CourseResponse.CourseDetailResponse getCourseDetail(Member member, Long courseId) {
        return null;
    }

    @Override
    public CourseResponse.CourseReviewListResponse getCourseReviews(Member member, Long courseId, ReviewSortType sortType) {
        return null;
    }

    @Override
    public CourseResponse.CoursePathResponse getCoursePath(Long courseId) {
        return null;
    }
}
