package com.follow_me.running_mate.domain.course.service;

import com.follow_me.running_mate.domain.course.dto.request.CourseRequest;
import com.follow_me.running_mate.domain.course.dto.response.CourseResponse;
import com.follow_me.running_mate.domain.course.entity.Course;
import com.follow_me.running_mate.domain.course.entity.CourseReview;
import com.follow_me.running_mate.domain.course.entity.CourseReviewImage;
import com.follow_me.running_mate.domain.course.exception.CourseErrorCode;
import com.follow_me.running_mate.domain.course.mapper.CourseEntityMapper;
import com.follow_me.running_mate.domain.course.repository.CourseReviewImageRepository;
import com.follow_me.running_mate.domain.course.repository.CourseReviewRepository;
import com.follow_me.running_mate.domain.enums.ReviewSortType;
import com.follow_me.running_mate.domain.member.entity.Member;
import com.follow_me.running_mate.global.common.service.S3ImageService;
import com.follow_me.running_mate.global.error.exception.CustomException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class CourseReviewServiceImpl implements CourseReviewService {

    private final CourseEntityMapper courseEntityMapper;
    private final CourseReviewRepository courseReviewRepository;
    private final CourseReviewImageRepository courseReviewImageRepository;

    private final S3ImageService s3ImageService;


    @Override
    public CourseReview save(
        Course course, Member member, CourseRequest.CreateReviewRequest request
    ) {
        return courseReviewRepository.save(
            courseEntityMapper.toCourseReview(course, member, request)
        );
    }

    @Override
    @Transactional
    public CourseReview delete(
        Long reviewId, Member member
    ) {
        CourseReview review = courseReviewRepository.getCourseReview(reviewId);

        if (!member.getId().equals(review.getWriter().getId())) {
            throw new CustomException(CourseErrorCode.UNAUTHORIZED_REVIEW);
        }

        review.getImages().forEach( image -> {
            s3ImageService.deleteImageFromS3(image.getUrl());
            courseReviewImageRepository.delete(image);
        });

        review.delete();

        return review;
    }

    @Override
    @Transactional
    public List<CourseReviewImage> saveImages(
        CourseReview courseReview, List<MultipartFile> images
    ) {
        return images.stream()
            .map(s3ImageService::upload)
            .map(url -> courseEntityMapper.toCourseReviewImage(courseReview, url))
            .map(courseReviewImageRepository::save)
            .toList();
    }

    @Override
    public Double getAverageRating(Course course) {
        return courseReviewRepository.findAverageRatingByCourse(course);
    }

    @Override
    public List<CourseReview> getRecentReviews(Course course) {
        return courseReviewRepository.findTop3ByCourseOrderByCreatedAtDesc(course);
    }

    @Override
    public List<CourseReview> getReviews(Course course, ReviewSortType sortType) {
        return sortType.sort(course, courseReviewRepository);
    }

    @Override
    public List<Integer> getReviewCounts(List<CourseResponse.ReviewInfo> reviews) {
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
