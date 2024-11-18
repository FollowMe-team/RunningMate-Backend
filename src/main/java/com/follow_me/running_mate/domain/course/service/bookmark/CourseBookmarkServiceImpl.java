package com.follow_me.running_mate.domain.course.service.bookmark;

import com.follow_me.running_mate.domain.course.entity.Course;
import com.follow_me.running_mate.domain.course.entity.CourseBookmark;
import com.follow_me.running_mate.domain.course.exception.CourseErrorCode;
import com.follow_me.running_mate.domain.course.mapper.CourseEntityMapper;
import com.follow_me.running_mate.domain.course.repository.CourseBookmarkRepository;
import com.follow_me.running_mate.domain.member.entity.Member;
import com.follow_me.running_mate.global.error.exception.CustomException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CourseBookmarkServiceImpl implements CourseBookmarkService {

    private final CourseEntityMapper courseEntityMapper;
    private final CourseBookmarkRepository courseBookmarkRepository;


    @Override
    @Transactional
    public void bookmarkCourse(Member member, Course course) {
        courseBookmarkRepository.findByMemberAndCourse(member, course)
            .ifPresentOrElse(
                existingBookmark -> {
                    if (existingBookmark.getIsBookmarked()) {
                        throw new CustomException(CourseErrorCode.ALREADY_BOOKMARKED);
                    }
                    existingBookmark.changeBookmark();
                },
                () -> handleNewBookmark(member, course)
            );
    }

    @Override
    @Transactional
    public void cancelBookmark(Member member, Course course) {
        courseBookmarkRepository.findByMemberAndCourse(member, course)
            .ifPresentOrElse(
                existingBookmark -> {
                    if (!existingBookmark.getIsBookmarked()) {
                        throw new CustomException(CourseErrorCode.NOT_BOOKMARKED);
                    }
                    existingBookmark.changeBookmark();
                },
                () -> { throw new CustomException(CourseErrorCode.NOT_BOOKMARKED); }
            );
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isBookmarked(Member member, Course course) {
        return courseBookmarkRepository.findByMemberAndCourse(member, course)
            .map(CourseBookmark::getIsBookmarked)
            .orElse(false);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Course> getBookmarkedCourses(Member member) {
        return courseBookmarkRepository.findAllByMemberAndIsBookmarkedTrue(member).stream()
            .map(CourseBookmark::getCourse)
            .toList();
    }

    private void handleNewBookmark(Member member, Course course) {
        if (hasReachedBookmarkLimit(member)) {
            throw new CustomException(CourseErrorCode.OVER_MAX_BOOKMARK);
        }
        CourseBookmark newBookmark = courseEntityMapper.toCourseBookmark(course, member);
        courseBookmarkRepository.save(newBookmark);
    }

    private boolean hasReachedBookmarkLimit(Member member) {
        long bookmarkCount = courseBookmarkRepository.countByMemberAndIsBookmarkedTrue(member);
        return bookmarkCount >= 3;
    }
}
