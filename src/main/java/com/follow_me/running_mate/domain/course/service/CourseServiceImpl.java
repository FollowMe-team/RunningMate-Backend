package com.follow_me.running_mate.domain.course.service;

import com.follow_me.running_mate.domain.course.dto.request.CourseRequest;
import com.follow_me.running_mate.domain.course.dto.response.CourseResponse;
import com.follow_me.running_mate.domain.course.entity.Course;
import com.follow_me.running_mate.domain.course.entity.CourseOption;
import com.follow_me.running_mate.domain.course.entity.CoursePoint;
import com.follow_me.running_mate.domain.course.entity.CourseReview;
import com.follow_me.running_mate.domain.course.entity.CourseReviewImage;
import com.follow_me.running_mate.domain.course.mapper.CourseEntityMapper;
import com.follow_me.running_mate.domain.course.mapper.CourseResponseMapper;
import com.follow_me.running_mate.domain.course.repository.CourseRepository;
import com.follow_me.running_mate.domain.course.service.bookmark.CourseBookmarkService;
import com.follow_me.running_mate.domain.course.service.image.CourseImageService;
import com.follow_me.running_mate.domain.course.service.option.CourseOptionService;
import com.follow_me.running_mate.domain.course.service.point.CoursePointService;
import com.follow_me.running_mate.domain.course.service.record.CourseRecordService;
import com.follow_me.running_mate.domain.course.service.review.CourseReviewService;
import com.follow_me.running_mate.domain.crew.service.CrewService;
import com.follow_me.running_mate.domain.enums.CourseOptionType;
import com.follow_me.running_mate.domain.enums.Difficulty;
import com.follow_me.running_mate.domain.enums.Ranking;
import com.follow_me.running_mate.domain.enums.ReviewSortType;
import com.follow_me.running_mate.domain.enums.RunningGoal;
import com.follow_me.running_mate.domain.enums.Status;
import com.follow_me.running_mate.domain.member.entity.Member;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseResponseMapper courseResponseMapper;
    private final CourseEntityMapper courseEntityMapper;

    private final CourseRepository courseRepository;

    private final CourseRecordService courseRecordService;
    private final CourseReviewService courseReviewService;
    private final CourseBookmarkService courseBookmarkService;
    private final CourseOptionService courseOptionService;
    private final CourseImageService courseImageService;
    private final CoursePointService coursePointService;
    private final CrewService crewService;
    private final LambdaService lambdaService;


    @Override
    @Transactional
    public CourseResponse.CourseIdResponse createCourse(
        Member member, CourseRequest.CreateCourseRequest request,
        MultipartFile representativeImage, MultipartFile startImage, MultipartFile endImage
    ) {
        log.info("코스 생성 시작: memberId={}", member.getId());

        Course course = courseEntityMapper.toCourse(request, member);
        course = courseRepository.save(course);
        log.info("코스 기본 정보 저장 완료: courseId={}", course.getId());

        courseImageService.saveCourseImages(course, representativeImage, startImage, endImage);
        List<CourseOption> courseOptions = courseOptionService.saveAll(course, request.getOptions());
        course.addOptions(courseOptions);
        List<CoursePoint> coursePoints = coursePointService.saveCoursePoints(course, request.getCoursePoints());
        log.info("코스 상세 정보 저장 완료: courseId={}", course.getId());

        final Long courseId = course.getId();
        CompletableFuture.runAsync(() -> {
            try {
                log.info("Lambda 분석 시작: courseId={}", courseId);
                updateCourseStatus(courseId, Status.ANALYZING);
                log.info("상태 업데이트 - ANALYZING: courseId={}", courseId);

                lambdaService.invokeCourseDifficultyLambda(
                    courseRepository.getCourseNotApproved(courseId),
                    coursePointService.getCoursePointsByCourseId(courseId)
                );
                log.info("Lambda 분석 완료: courseId={}", courseId);

                updateCourseStatus(courseId, Status.WAIT);
                log.info("상태 업데이트 - WAIT: courseId={}", courseId);
            } catch (Exception e) {
                log.error("Lambda 분석 실패: courseId={}", courseId, e);
                updateCourseStatus(courseId, Status.READY);
            }
        });

        log.info("코스 생성 API 응답: courseId={}", course.getId());
        return new CourseResponse.CourseIdResponse(course.getId());
    }


    @Override
    @Transactional
    public CourseResponse.CourseRecordIdResponse createCourseRecord(
        Member member, Long courseId, CourseRequest.CreateCourseRecordRequest request
    ) {
        Course course = courseRepository.getCourse(courseId);
        course.updateRunningCount();

        return courseRecordService.createCourseRecord(member, course, request);
    }

    @Override
    @Transactional
    public void bookmarkCourse(Member member, Long courseId) {
        Course course = courseRepository.getCourse(courseId);

        courseBookmarkService.bookmarkCourse(member, course);
    }

    @Override
    @Transactional
    public void bookmarkCancelCourse(Member member, Long courseId) {
        Course course = courseRepository.getCourse(courseId);

        courseBookmarkService.cancelBookmark(member, course);
    }

    @Override
    @Transactional
    public CourseResponse.ReviewIdResponse createCourseReview(
        Member member, Long courseId, CourseRequest.CreateReviewRequest request, List<MultipartFile> images
    ) {
        Course course = courseRepository.getCourse(courseId);

        CourseReview courseReview = courseReviewService.save(course, member, request);

        if (images != null && !images.isEmpty()) {
            List<CourseReviewImage> reviewImages = courseReviewService.saveImages(courseReview, images);
            reviewImages.forEach(courseReview::addImage);
        }

        return new CourseResponse.ReviewIdResponse(courseReview.getId());
    }

    @Override
    @Transactional
    public CourseResponse.ReviewIdResponse deleteCourseReview(Member member, Long reviewId) {
        CourseReview courseReview = courseReviewService.delete(reviewId, member);
        return new CourseResponse.ReviewIdResponse(courseReview.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public CourseResponse.CourseListResponse getRecentCourses(Member member) {

        List<Course> recentCourses = courseRecordService.getRecentCourses(member);

        List<CourseResponse.SummaryInfo> courses = recentCourses.stream().map(course ->
            courseResponseMapper.toSummaryInfo(
                course,
                courseReviewService.getAverageRating(course),
                course.getRunningCount(),
                courseBookmarkService.isBookmarked(member, course),
                courseOptionService.getCourseOptions(course),
                coursePointService.getCoursePoints(course)
            )).toList();

        return new CourseResponse.CourseListResponse(courses);
    }

    @Override
    @Transactional(readOnly = true)
    public CourseResponse.CourseListResponse getBookmarkedCourses(Member member) {

        List<Course> bookmarkedCourses = courseBookmarkService.getBookmarkedCourses(member);

        List<CourseResponse.SummaryInfo> courses = bookmarkedCourses.stream().map(course ->
            courseResponseMapper.toSummaryInfo(
                course,
                courseReviewService.getAverageRating(course),
                course.getRunningCount(),
                courseBookmarkService.isBookmarked(member, course),
                courseOptionService.getCourseOptions(course),
                coursePointService.getCoursePoints(course)
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
                courseReviewService.getAverageRating(course),
                course.getRunningCount(),
                courseBookmarkService.isBookmarked(member, course),
                courseOptionService.getCourseOptions(course),
                coursePointService.getCoursePoints(course)
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
            courseOptionService.getOptionByRunningGoal(runningGoal) : List.of();

        List<Course> recommendedCourses = courseRepository.recommendCourses(
            latitude, longitude, radius, effectiveDifficulty.name(), goalOptions);

        List<CourseResponse.SummaryInfo> courses = recommendedCourses.stream().map(course ->
            courseResponseMapper.toSummaryInfo(
                course,
                courseReviewService.getAverageRating(course),
                course.getRunningCount(),
                courseBookmarkService.isBookmarked(member, course),
                courseOptionService.getCourseOptions(course),
                coursePointService.getCoursePoints(course)
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
                courseReviewService.getAverageRating(course),
                course.getRunningCount(),
                courseBookmarkService.isBookmarked(member, course),
                courseOptionService.getCourseOptions(course),
                coursePointService.getCoursePoints(course)
            )).toList();

        return new CourseResponse.CourseListResponse(courses);
    }

    @Override
    @Transactional(readOnly = true)
    public CourseResponse.CourseDetailResponse getCourseDetail(Member member, Long courseId) {

        Course course = courseRepository.getCourse(courseId);

        return courseResponseMapper.toCourseDetailResponse(
            course,
            courseReviewService.getAverageRating(course),
            courseBookmarkService.isBookmarked(member, course),
            courseImageService.getCourseImages(course),
            courseOptionService.getCourseOptions(course),
            coursePointService.getCoursePoints(course),
            courseResponseMapper.toCrewInfos(crewService.getCrewByCourse(course)),
            courseResponseMapper.toReviewInfos(courseReviewService.getRecentReviews(course), member)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public CourseResponse.CourseReviewListResponse getCourseReviews(
        Member member, Long courseId, ReviewSortType sortType
    ) {
        Course course = courseRepository.getCourse(courseId);

        List<CourseResponse.ReviewInfo> reviews = courseResponseMapper.toReviewInfos(
            courseReviewService.getReviews(course, sortType), member
        );

        return courseResponseMapper.toCourseReviewListResponse(
            reviews,
            courseReviewService.getAverageRating(course),
            courseReviewService.getReviewCounts(reviews)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public CourseResponse.CoursePathResponse getCoursePath(Long courseId) {
        Course course = courseRepository.getCourse(courseId);

        List<CoursePoint> coursePoints = coursePointService.getCoursePoints(course);

        return new CourseResponse.CoursePathResponse(
            coursePoints.stream()
                .map(courseResponseMapper::toCoursePointDetail)
                .toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CourseResponse.CheckCourseNameResponse checkCourseName(String name) {
        return new CourseResponse.CheckCourseNameResponse(
            courseRepository.existsByName(name)
        );
    }

    @Transactional
    protected void updateCourseStatus(Long courseId, Status status) {
        Course course = courseRepository.getCourseNotApproved(courseId);
        course.updateStatus(status);
        courseRepository.save(course);
    }

    // 사용자 ranking에 따른 기본 난이도 설정
    private Difficulty getDefaultDifficultyByRanking(Ranking ranking) {
        return switch (ranking) {
            case JOGGER, RUNNER,BRONZE,SILVER,NO_RANK,GOLD,PLATINUM,DIAMOND,ELITE,LEGEND -> Difficulty.EASY;
            case RACER, SPRINTER -> Difficulty.NORMAL;
            case MARATHONER, ULTRA_RUNNER, IRON_LEGS, SPEED_DEMON -> Difficulty.HARD;
        };
    }
}
