package com.follow_me.running_mate.domain.course.repository;

import com.follow_me.running_mate.domain.course.entity.Course;
import com.follow_me.running_mate.domain.course.entity.CourseBookmark;
import com.follow_me.running_mate.domain.member.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseBookmarkRepository extends JpaRepository<CourseBookmark, Long> {
    Optional<CourseBookmark> findByMemberAndCourse(Member member, Course course);
}
