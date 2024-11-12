package com.follow_me.running_mate.domain.course.service;

import com.follow_me.running_mate.domain.course.dto.response.CourseResponse;
import com.follow_me.running_mate.domain.course.entity.Course;
import com.follow_me.running_mate.domain.course.entity.CourseBookmark;
import com.follow_me.running_mate.domain.course.entity.CourseImage;
import com.follow_me.running_mate.domain.course.entity.CoursePoint;
import com.follow_me.running_mate.domain.course.entity.CourseReviewImage;
import com.follow_me.running_mate.domain.course.mapper.CourseMapper;
import com.follow_me.running_mate.domain.course.repository.CourseBookmarkRepository;
import com.follow_me.running_mate.domain.course.repository.CourseImageRepository;
import com.follow_me.running_mate.domain.course.repository.CourseOptionRepository;
import com.follow_me.running_mate.domain.course.repository.CoursePointRepository;
import com.follow_me.running_mate.domain.course.repository.CourseRepository;
import com.follow_me.running_mate.domain.course.repository.CourseReviewRepository;
import com.follow_me.running_mate.domain.crew.service.CrewService;
import com.follow_me.running_mate.domain.enums.CourseOptionType;
import com.follow_me.running_mate.domain.enums.Difficulty;
import com.follow_me.running_mate.domain.enums.ReviewSortType;
import com.follow_me.running_mate.domain.enums.RunningGoal;
import com.follow_me.running_mate.domain.member.entity.Member;
import com.follow_me.running_mate.domain.record.service.RunningRecordService;
import com.follow_me.running_mate.domain.course.repository.CourseReviewImageRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
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
    private final CourseImageRepository courseImageRepository;
    private final CourseReviewImageRepository courseReviewImageRepository;

    private final RunningRecordService runningRecordService;
    private final CrewService crewService;


    @Override
    public CourseResponse.CourseListResponse getRecentCourses(Member member) {

        List<Course> recentCourses = runningRecordService.getRecentCourses(member);

        List<CourseResponse.SummaryInfo> courses = recentCourses.stream().map(course ->
            courseMapper.toSummaryInfo(
                course,
                courseReviewRepository.findAverageRatingByCourse(course),
                course.getRunningCount(),
                isBookmarkedCourse(member, course),
                courseOptionRepository.findAllByCourse(course),
                coursePointRepository.findAllByCourseOrderBySequenceNumberAsc(course)
            )).toList();

        return new CourseResponse.CourseListResponse(courses);
    }

    @Override
    public CourseResponse.CourseListResponse getBookmarkedCourses(Member member) {

        List<Course> bookmarkedCourses = getBookmarkedCourseByMember(member);

        List<CourseResponse.SummaryInfo> courses = bookmarkedCourses.stream().map(course ->
            courseMapper.toSummaryInfo(
                course,
                courseReviewRepository.findAverageRatingByCourse(course),
                course.getRunningCount(),
                isBookmarkedCourse(member, course),
                courseOptionRepository.findAllByCourse(course),
                coursePointRepository.findAllByCourseOrderBySequenceNumberAsc(course)
            )).toList();

        return new CourseResponse.CourseListResponse(courses);
    }

    @Override
    public CourseResponse.MyCourseListResponse getMyCourses(Member member) {

        List<Course> myCourses = courseRepository.findAllByWriterOrderByCreatedAtDesc(member);

        List<CourseResponse.MyCourseInfo> courses = myCourses.stream().map(course ->
            courseMapper.toMyCourseInfo(
                course,
                courseReviewRepository.findAverageRatingByCourse(course),
                course.getRunningCount(),
                isBookmarkedCourse(member, course),
                courseOptionRepository.findAllByCourse(course),
                coursePointRepository.findAllByCourseOrderBySequenceNumberAsc(course)
            )).toList();

        return new CourseResponse.MyCourseListResponse(courses);
    }

    @Override
    public CourseResponse.CourseListResponse recommendedCourses(
        Member member, Double latitude, Double longitude, List<Difficulty> difficulties, List<RunningGoal> runningGoals
    ) {
        return null;
    }

    @Override
    public CourseResponse.CourseListResponse searchCourses(
        Member member, String keyword, Double latitude,
        Double longitude, List<Difficulty> difficulties, List<CourseOptionType> options
    ) {
        // 위치 반경 기본값 (단위: 미터) 예시로 5000m 설정
        double radius = 5000.0;

        List<Course> searchCourses = courseRepository.searchCourses(
            keyword, latitude, longitude, radius, difficulties, options
        );

        List<CourseResponse.SummaryInfo> courses = searchCourses.stream().map(course ->
            courseMapper.toSummaryInfo(
                course,
                courseReviewRepository.findAverageRatingByCourse(course),
                course.getRunningCount(),
                isBookmarkedCourse(member, course),
                courseOptionRepository.findAllByCourse(course),
                coursePointRepository.findAllByCourseOrderBySequenceNumberAsc(course)
            )).toList();

        return new CourseResponse.CourseListResponse(courses);
    }

    @Override
    public CourseResponse.CourseDetailResponse getCourseDetail(Member member, Long courseId) {

        Course course = courseRepository.getCourse(courseId);

        return courseMapper.toCourseDetailResponse(
            course,
            courseReviewRepository.findAverageRatingByCourse(course),
            isBookmarkedCourse(member, course),
            courseImageRepository.findAllByCourse(course).stream()
                .map(CourseImage::getUrl)
                .toList(),
            courseOptionRepository.findAllByCourse(course),
            coursePointRepository.findAllByCourseOrderBySequenceNumberAsc(course),
            courseMapper.toCrewInfos(crewService.getCrewByCourse(course)),
            courseMapper.toReviewInfos(courseReviewRepository.findTop3ByCourseOrderByCreatedAtDesc(course), member),
            getRatingCounts(courseMapper.toReviewInfos(courseReviewRepository.findAllByCourse(course), member))
        );
    }

    @Override
    public CourseResponse.CourseReviewListResponse getCourseReviews(
        Member member, Long courseId, ReviewSortType sortType
    ) {
        Course course = courseRepository.getCourse(courseId);

        List<CourseResponse.ReviewInfo> reviews = courseMapper.toReviewInfos(
            sortType.sort(course, courseReviewRepository), member
        );

        return courseMapper.toCourseReviewListResponse(
            reviews,
            courseReviewRepository.findAverageRatingByCourse(course),
            getRatingCounts(reviews)
        );
    }

    @Override
    public CourseResponse.CoursePathResponse getCoursePath(Long courseId) {
        Course course = courseRepository.getCourse(courseId);

        List<CoursePoint> coursePoints = coursePointRepository.findAllByCourseOrderBySequenceNumberAsc(course);

        return new CourseResponse.CoursePathResponse(
            coursePoints.stream()
                .map(courseMapper::toCoursePointDetail)
                .toList());
    }

    private boolean isBookmarkedCourse(Member member, Course course) {
        Optional<CourseBookmark> courseBookmark = courseBookmarkRepository.findByMemberAndCourse(member, course);
        return courseBookmark.map(CourseBookmark::getIsBookmarked).orElse(false);
    }

    private List<Course> getBookmarkedCourseByMember(Member member) {
        return courseBookmarkRepository.findAllByMemberAndIsBookmarkedTrue(member).stream()
            .map(CourseBookmark::getCourse)
            .toList();
    }

    private List<Integer> getRatingCounts(List<CourseResponse.ReviewInfo> reviews) {
        // 리뷰 리스트를 평점별로 그룹화하여 개수를 세기
        Map<Integer, Long> ratingCountMap = reviews.stream()
            .collect(Collectors.groupingBy(
                review -> review.getRating().intValue(),
                Collectors.counting()
            ));

        // 각 평점(5점 ~ 1점)별 개수를 순서대로 List에 추가
        List<Integer> ratingCounts = new ArrayList<>();
        for (int i = 5; i >= 1; i--) {
            ratingCounts.add(ratingCountMap.getOrDefault(i, 0L).intValue());
        }
        return ratingCounts;
    }
}
