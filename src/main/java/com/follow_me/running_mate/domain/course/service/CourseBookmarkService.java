package com.follow_me.running_mate.domain.course.service;

import com.follow_me.running_mate.domain.course.entity.Course;
import com.follow_me.running_mate.domain.member.entity.Member;
import java.util.List;

public interface CourseBookmarkService {
    void bookmarkCourse(Member member, Course course);
    void cancelBookmark(Member member, Course course);
    boolean isBookmarked(Member member, Course course);
    List<Course> getBookmarkedCourses(Member member);
}
