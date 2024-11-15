package com.follow_me.running_mate.domain.course.service;

import com.follow_me.running_mate.domain.course.dto.request.CourseRequest;
import com.follow_me.running_mate.domain.course.dto.response.CourseResponse;
import com.follow_me.running_mate.domain.course.entity.Course;
import com.follow_me.running_mate.domain.course.entity.CourseBookmark;
import com.follow_me.running_mate.domain.course.entity.CourseImage;
import com.follow_me.running_mate.domain.course.entity.CoursePoint;
import com.follow_me.running_mate.domain.course.entity.CourseReview;
import com.follow_me.running_mate.domain.course.entity.CourseReviewImage;
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
import com.follow_me.running_mate.domain.enums.CourseImageType;
import com.follow_me.running_mate.domain.enums.CourseOptionType;
import com.follow_me.running_mate.domain.enums.Difficulty;
import com.follow_me.running_mate.domain.enums.Ranking;
import com.follow_me.running_mate.domain.enums.ReviewSortType;
import com.follow_me.running_mate.domain.enums.RunningGoal;
import com.follow_me.running_mate.domain.member.entity.Member;
import com.follow_me.running_mate.domain.course.repository.CourseReviewImageRepository;
import com.follow_me.running_mate.global.common.service.S3ImageService;
import com.follow_me.running_mate.global.error.exception.CustomException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseResponseMapper courseResponseMapper;
    private final CourseEntityMapper courseEntityMapper;

    private final CourseRepository courseRepository;
    private final CoursePointRepository coursePointRepository;
    private final CourseImageRepository courseImageRepository;

    private final CourseRecordService courseRecordService;
    private final CourseReviewService courseReviewService;
    private final CourseBookmarkService courseBookmarkService;
    private final CourseOptionService courseOptionService;
    private final CrewService crewService;
    private final S3ImageService s3ImageService;
    private final LambdaService lambdaService;


    @Override
    @Transactional
    public CourseResponse.CourseIdResponse createCourse(
        Member member, CourseRequest.CreateCourseRequest request,
        MultipartFile representativeImage, MultipartFile startImage, MultipartFile endImage
    ) {
        Course course = courseRepository.save(courseEntityMapper.toCourse(request, member));

        saveCourseImages(course, representativeImage, startImage, endImage);
        course.addOption(courseOptionService.getCourseOptions(course));
        saveCoursePoints(course, request.getCoursePoints());

        // 람다 호출: 난이도 및 기타 계산 (비동기)
        // TODO: 람다 완성되면 주석 해제
        // CompletableFuture.runAsync(() -> lambdaService.invokeCourseDifficultyLambda(course.getId()));

        return new CourseResponse.CourseIdResponse(course.getId());
    }

    @Override
    @Transactional
    public CourseResponse.CourseRecordIdResponse createCourseRecord(
        Member member, Long courseId, CourseRequest.CreateCourseRecordRequest request
    ) {
        Course course = courseRepository.getCourse(courseId);

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
                coursePointRepository.findAllByCourseOrderBySequenceNumberAsc(course)
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
                courseReviewService.getAverageRating(course),
                course.getRunningCount(),
                courseBookmarkService.isBookmarked(member, course),
                courseOptionService.getCourseOptions(course),
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
                courseReviewService.getAverageRating(course),
                course.getRunningCount(),
                courseBookmarkService.isBookmarked(member, course),
                courseOptionService.getCourseOptions(course),
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
            courseReviewService.getAverageRating(course),
            courseBookmarkService.isBookmarked(member, course),
            courseImageRepository.findAllByCourse(course).stream()
                .map(CourseImage::getUrl)
                .toList(),
            courseOptionService.getCourseOptions(course),
            coursePointRepository.findAllByCourseOrderBySequenceNumberAsc(course),
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

    @Override
    @Transactional(readOnly = true)
    public CourseResponse.CheckCourseNameResponse checkCourseName(String name) {
        return new CourseResponse.CheckCourseNameResponse(
            courseRepository.existsByName(name)
        );
    }

    // 코스 이미지 저장 메서드
    private void saveCourseImages(
        Course course, MultipartFile representativeImage, MultipartFile startImage, MultipartFile endImage
    ) {
        if (representativeImage != null) {
            String repImageUrl = s3ImageService.upload(representativeImage);
            courseImageRepository.save(
                courseEntityMapper.toCourseImage(course, repImageUrl, CourseImageType.REPRESENTATIVE)
            );
        }
        if (startImage != null) {
            String startImageUrl = s3ImageService.upload(startImage);
            courseImageRepository.save(
                courseEntityMapper.toCourseImage(course, startImageUrl, CourseImageType.START)
            );
        }
        if (endImage != null) {
            String endImageUrl = s3ImageService.upload(endImage);
            courseImageRepository.save(
                courseEntityMapper.toCourseImage(course, endImageUrl, CourseImageType.FINISH)
            );
        }
    }

    // 코스 포인트 저장 메서드
    private void saveCoursePoints(Course course, List<CourseRequest.CoursePointInfo> coursePoints) {
        for (int i = 0; i < coursePoints.size(); i++) {
            CoursePoint coursePoint = courseEntityMapper.toCoursePoint(course, coursePoints.get(i), i + 1);
            coursePointRepository.save(coursePoint);
        }
    }

    // 사용자 ranking에 따른 기본 난이도 설정
    private Difficulty getDefaultDifficultyByRanking(Ranking ranking) {
        return switch (ranking) {
            case JOGGER, RUNNER -> Difficulty.EASY;
            case RACER, SPRINTER -> Difficulty.NORMAL;
            case MARATHONER, ULTRA_RUNNER, IRON_LEGS, SPEED_DEMON -> Difficulty.HARD;
        };
    }

    private List<Integer> getRatingCounts(List<CourseResponse.ReviewInfo> reviews) {
        // 리뷰 리스트를 평점별로 그룹화하여 개수를 세기
        Map<Integer, Long> ratingCountMap = reviews.stream()
            .collect(Collectors.groupingBy(
                CourseResponse.ReviewInfo::getRating,
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
