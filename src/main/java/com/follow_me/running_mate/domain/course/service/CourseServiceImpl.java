package com.follow_me.running_mate.domain.course.service;

import com.follow_me.running_mate.domain.course.dto.response.CourseResponse;
import com.follow_me.running_mate.domain.course.entity.Course;
import com.follow_me.running_mate.domain.course.entity.CourseBookmark;
import com.follow_me.running_mate.domain.course.mapper.CourseMapper;
import com.follow_me.running_mate.domain.course.repository.CourseBookmarkRepository;
import com.follow_me.running_mate.domain.course.repository.CourseOptionRepository;
import com.follow_me.running_mate.domain.course.repository.CoursePointRepository;
import com.follow_me.running_mate.domain.course.repository.CourseRepository;
import com.follow_me.running_mate.domain.course.repository.CourseReviewRepository;
import com.follow_me.running_mate.domain.enums.CourseOptionType;
import com.follow_me.running_mate.domain.enums.Difficulty;
import com.follow_me.running_mate.domain.enums.ReviewSortType;
import com.follow_me.running_mate.domain.enums.RunningGoal;
import com.follow_me.running_mate.domain.member.entity.Member;
import com.follow_me.running_mate.domain.record.entity.RunningRecord;
import com.follow_me.running_mate.domain.record.service.RunningRecordService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseMapper courseMapper;
    private final CourseRepository courseRepository;
    private final CourseReviewRepository courseReviewRepository;
    private final CourseBookmarkRepository courseBookmarkRepository;
    private final CourseOptionRepository courseOptionRepository;
    private final CoursePointRepository coursePointRepository;
    private final RunningRecordService runningRecordService;


    @Override
    public CourseResponse.CourseListResponse getRecentCourses(Member member) {

        List<RunningRecord> recentCourses = runningRecordService.getRecentCourses(member);

        List<CourseResponse.SummaryInfo> courses = recentCourses.stream().map(runningRecord ->
            courseMapper.toSummaryInfo(
                runningRecord.getCourse(),
                courseReviewRepository.findAverageRatingByCourse(runningRecord.getCourse()),
                runningRecord.getRunningCount(),
                isBookmarkedCourse(member, runningRecord.getCourse()),
                courseOptionRepository.findAllByCourse(runningRecord.getCourse()),
                coursePointRepository.findAllByCourseOrderBySequenceNumberAsc(runningRecord.getCourse())
            )).toList();

        return new CourseResponse.CourseListResponse(courses);
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

    private boolean isBookmarkedCourse(Member member, Course course) {
        Optional<CourseBookmark> courseBookmark = courseBookmarkRepository.findByMemberAndCourse(member, course);
        return courseBookmark.map(CourseBookmark::isBookmarked).orElse(false);
    }
}
