package com.follow_me.running_mate.domain.course.repository;

import com.follow_me.running_mate.domain.course.entity.Course;
import com.follow_me.running_mate.domain.course.exception.CourseErrorCode;
import com.follow_me.running_mate.domain.enums.Status;
import com.follow_me.running_mate.domain.member.entity.Member;
import com.follow_me.running_mate.global.error.exception.CustomException;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CourseRepository extends JpaRepository<Course, Long> {

    default Course getCourse(Long id) {
        return findById(id)
            .map(course -> {
                if (course.getStatus() != Status.COMPLETE) {
                    throw new CustomException(CourseErrorCode.NOT_APPROVED);
                }
                return course;
            })
            .orElseThrow(() -> new CustomException(CourseErrorCode.NOT_FOUND));
    }

    default Course getCourseNotApproved(Long id) {
        return findById(id)
            .orElseThrow(() -> new CustomException(CourseErrorCode.NOT_FOUND));
    }

    boolean existsByName(String name);

    List<Course> findAllByWriterOrderByCreatedAtDesc(Member writer);

    @Query(value = "SELECT DISTINCT c.* FROM course c " +
        "JOIN course_option o ON c.id = o.course_id " +
        "WHERE (:keyword IS NULL OR c.name ILIKE CONCAT('%', :keyword, '%') " +
        "OR c.description ILIKE CONCAT('%', :keyword, '%') " +
        "OR c.city ILIKE CONCAT('%', :keyword, '%') " +
        "OR c.district ILIKE CONCAT('%', :keyword, '%')) " +
        "AND (:latitude IS NULL OR ST_DWithin(c.start_point, ST_MakePoint(:longitude, :latitude)::geography, :radius)) " +
        "AND c.status = 'COMPLETE' " +
        "AND (:difficulties IS NULL OR c.difficulty IN (:difficulties)) " +
        "AND (:options IS NULL OR o.type IN (:options))",
        nativeQuery = true)
    List<Course> searchCourses(
        @Param("keyword") String keyword,
        @Param("latitude") Double latitude,
        @Param("longitude") Double longitude,
        @Param("radius") Double radius,
        @Param("difficulties") List<String> difficulties,
        @Param("options") List<String> options);
}
