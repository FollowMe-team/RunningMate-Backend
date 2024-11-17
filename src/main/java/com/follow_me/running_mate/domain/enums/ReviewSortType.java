package com.follow_me.running_mate.domain.enums;

import com.follow_me.running_mate.domain.course.entity.Course;
import com.follow_me.running_mate.domain.course.entity.CourseReview;
import com.follow_me.running_mate.domain.course.repository.CourseReviewRepository;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReviewSortType {
    LATEST("최신순") {
        @Override
        public List<CourseReview> sort(Course course, CourseReviewRepository repository) {
            return repository.findByCourseOrderByCreatedAtDesc(course);
        }
    },
    OLDEST("오래된순") {
        @Override
        public List<CourseReview> sort(Course course, CourseReviewRepository repository) {
            return repository.findByCourseOrderByCreatedAtAsc(course);
        }
    },
    HIGHEST_RATING("높은 평점순") {
        @Override
        public List<CourseReview> sort(Course course, CourseReviewRepository repository) {
            return repository.findByCourseOrderByRatingDesc(course);
        }
    },
    LOWEST_RATING("낮은 평점순") {
        @Override
        public List<CourseReview> sort(Course course, CourseReviewRepository repository) {
            return repository.findByCourseOrderByRatingAsc(course);
        }
    };

    private final String description;

    public abstract List<CourseReview> sort(Course course, CourseReviewRepository repository);
}
