package com.follow_me.running_mate.domain.course.service;

import com.follow_me.running_mate.domain.course.dto.response.CourseResponse;
import com.follow_me.running_mate.domain.course.entity.Course;
import com.follow_me.running_mate.domain.course.entity.CourseBookmark;
import com.follow_me.running_mate.domain.course.entity.CourseImage;
import com.follow_me.running_mate.domain.course.entity.CoursePoint;
import com.follow_me.running_mate.domain.course.exception.CourseErrorCode;
import com.follow_me.running_mate.domain.course.mapper.CourseEntityMapper;
import com.follow_me.running_mate.domain.course.mapper.CourseResponseMapper;
import com.follow_me.running_mate.domain.course.repository.CourseBookmarkRepository;
import com.follow_me.running_mate.domain.course.repository.CourseImageRepository;
import com.follow_me.running_mate.domain.course.repository.CourseOptionRepository;
import com.follow_me.running_mate.domain.course.repository.CoursePointRepository;
import com.follow_me.running_mate.domain.course.repository.CourseRepository;
import com.follow_me.running_mate.domain.course.repository.CourseReviewRepository;
import com.follow_me.running_mate.domain.crew.service.CrewService;
import com.follow_me.running_mate.domain.enums.CourseOptionType;
import com.follow_me.running_mate.domain.enums.Difficulty;
import com.follow_me.running_mate.domain.enums.Ranking;
import com.follow_me.running_mate.domain.enums.ReviewSortType;
import com.follow_me.running_mate.domain.enums.RunningGoal;
import com.follow_me.running_mate.domain.member.entity.Member;
import com.follow_me.running_mate.domain.record.service.RunningRecordService;
import com.follow_me.running_mate.domain.course.repository.CourseReviewImageRepository;
import com.follow_me.running_mate.global.error.exception.CustomException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseResponseMapper courseResponseMapper;
    private final CourseEntityMapper courseEntityMapper;

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
    @Transactional
    public void bookmarkCourse(Member member, Long courseId) {
        Course course = courseRepository.getCourse(courseId);

        // TODO: 즐겨찾기가 이미 3개 이상인 경우 예외 처리

        courseBookmarkRepository.findByMemberAndCourse(member, course).ifPresentOrElse(
            existingBookmark -> {
                if (existingBookmark.getIsBookmarked()) {
                    throw new CustomException(CourseErrorCode.ALREADY_BOOKMARKED);
                }
                existingBookmark.changeBookmark();
            },
            () -> {
                CourseBookmark newBookmark = courseEntityMapper.toCourseBookmark(course, member);
                courseBookmarkRepository.save(newBookmark);
            }
        );
    }

    @Override
    @Transactional
    public void bookmarkCancelCourse(Member member, Long courseId) {
        Course course = courseRepository.getCourse(courseId);

        courseBookmarkRepository.findByMemberAndCourse(member, course).ifPresentOrElse(
            existingBookmark -> {
                if (!existingBookmark.getIsBookmarked()) {
                    throw new CustomException(CourseErrorCode.NOT_BOOKMARKED);
                }
                existingBookmark.changeBookmark();
            },
            () -> {
                throw new CustomException(CourseErrorCode.NOT_BOOKMARKED);
            }
        );
    }

    @Override
    @Transactional(readOnly = true)
    public CourseResponse.CourseListResponse getRecentCourses(Member member) {

        List<Course> recentCourses = runningRecordService.getRecentCourses(member);

        List<CourseResponse.SummaryInfo> courses = recentCourses.stream().map(course ->
            courseResponseMapper.toSummaryInfo(
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
    @Transactional(readOnly = true)
    public CourseResponse.CourseListResponse getBookmarkedCourses(Member member) {

        List<Course> bookmarkedCourses = getBookmarkedCourseByMember(member);

        List<CourseResponse.SummaryInfo> courses = bookmarkedCourses.stream().map(course ->
            courseResponseMapper.toSummaryInfo(
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
    @Transactional(readOnly = true)
    public CourseResponse.MyCourseListResponse getMyCourses(Member member) {

        List<Course> myCourses = courseRepository.findAllByWriterOrderByCreatedAtDesc(member);

        List<CourseResponse.MyCourseInfo> courses = myCourses.stream().map(course ->
            courseResponseMapper.toMyCourseInfo(
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
    @Transactional(readOnly = true)
    public CourseResponse.CourseListResponse recommendedCourses(
        Member member, Double latitude, Double longitude, Difficulty difficulty, RunningGoal runningGoal) {

        // 위치 반경 기본값 (단위: 미터)
        double radius = 50000.0;

        // 사용자 ranking을 기준으로 기본 난이도 설정
        Difficulty effectiveDifficulty =
            (difficulty != null) ? difficulty : getDefaultDifficultyByRanking(member.getRanking());

        // 러닝 목표에 맞는 옵션 필터링
        List<String> goalOptions = (runningGoal != null) ?
            getOptionsByRunningGoal(runningGoal).stream()
                .map(CourseOptionType::name)
                .toList() : List.of();

        List<Course> recommendedCourses = courseRepository.recommendCourses(
            latitude, longitude, radius, effectiveDifficulty.name(), goalOptions);

        List<CourseResponse.SummaryInfo> courses = recommendedCourses.stream().map(course ->
            courseResponseMapper.toSummaryInfo(
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
    @Transactional(readOnly = true)
    public CourseResponse.CourseListResponse searchCourses(
        Member member, String keyword, Double latitude,
        Double longitude, List<Difficulty> difficulties, List<CourseOptionType> options
    ) {
        // 위치 반경 기본값 (단위: 미터)
        double radius = 50000.0;

        List<String> difficultyList = (difficulties != null) ? difficulties.stream()
            .map(Difficulty::name)
            .toList() : List.of();

        List<String> optionsList = (options != null) ? options.stream()
            .map(CourseOptionType::name)
            .toList() : List.of();

        List<Course> searchCourses = courseRepository.searchCourses(
            keyword, latitude, longitude, radius, difficultyList, optionsList
        );

        List<CourseResponse.SummaryInfo> courses = searchCourses.stream().map(course ->
            courseResponseMapper.toSummaryInfo(
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
    @Transactional(readOnly = true)
    public CourseResponse.CourseDetailResponse getCourseDetail(Member member, Long courseId) {

        Course course = courseRepository.getCourse(courseId);

        return courseResponseMapper.toCourseDetailResponse(
            course,
            courseReviewRepository.findAverageRatingByCourse(course),
            isBookmarkedCourse(member, course),
            courseImageRepository.findAllByCourse(course).stream()
                .map(CourseImage::getUrl)
                .toList(),
            courseOptionRepository.findAllByCourse(course),
            coursePointRepository.findAllByCourseOrderBySequenceNumberAsc(course),
            courseResponseMapper.toCrewInfos(crewService.getCrewByCourse(course)),
            courseResponseMapper.toReviewInfos(courseReviewRepository.findTop3ByCourseOrderByCreatedAtDesc(course), member),
            getRatingCounts(courseResponseMapper.toReviewInfos(courseReviewRepository.findAllByCourse(course), member))
        );
    }

    @Override
    @Transactional(readOnly = true)
    public CourseResponse.CourseReviewListResponse getCourseReviews(
        Member member, Long courseId, ReviewSortType sortType
    ) {
        Course course = courseRepository.getCourse(courseId);

        List<CourseResponse.ReviewInfo> reviews = courseResponseMapper.toReviewInfos(
            sortType.sort(course, courseReviewRepository), member
        );

        return courseResponseMapper.toCourseReviewListResponse(
            reviews,
            courseReviewRepository.findAverageRatingByCourse(course),
            getRatingCounts(reviews)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public CourseResponse.CoursePathResponse getCoursePath(Long courseId) {
        Course course = courseRepository.getCourse(courseId);

        List<CoursePoint> coursePoints = coursePointRepository.findAllByCourseOrderBySequenceNumberAsc(course);

        return new CourseResponse.CoursePathResponse(
            coursePoints.stream()
                .map(courseResponseMapper::toCoursePointDetail)
                .toList());
    }

    // 사용자 ranking에 따른 기본 난이도 설정
    private Difficulty getDefaultDifficultyByRanking(Ranking ranking) {
        return switch (ranking) {
            case JOGGER, RUNNER -> Difficulty.EASY;
            case RACER, SPRINTER -> Difficulty.NORMAL;
            case MARATHONER, ULTRA_RUNNER, IRON_LEGS, SPEED_DEMON -> Difficulty.HARD;
        };
    }

    // 러닝 목표에 따른 추천 옵션 필터링
    private List<CourseOptionType> getOptionsByRunningGoal(RunningGoal runningGoal) {
        return switch (runningGoal) {
            case WEIGHT_LOSS ->
                List.of(CourseOptionType.GRADIENT_MIDDLE, CourseOptionType.PARK, CourseOptionType.TRAIL);
            case ENDURANCE ->
                List.of(CourseOptionType.MOUNTAIN, CourseOptionType.FOREST, CourseOptionType.GRADIENT_HIGH);
            case SPEED -> List.of(CourseOptionType.TRACK, CourseOptionType.GRADIENT_NONE, CourseOptionType.CITYSCAPE);
            default -> List.of(); // 목표가 없으면 모든 코스 허용
        };
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
