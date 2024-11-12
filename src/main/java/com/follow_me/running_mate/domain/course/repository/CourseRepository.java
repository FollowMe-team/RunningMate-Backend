package com.follow_me.running_mate.domain.course.repository;

import com.follow_me.running_mate.domain.course.entity.Course;
import com.follow_me.running_mate.domain.course.exception.CourseErrorCode;
import com.follow_me.running_mate.domain.enums.CourseOptionType;
import com.follow_me.running_mate.domain.enums.Difficulty;
import com.follow_me.running_mate.domain.member.entity.Member;
import com.follow_me.running_mate.global.error.exception.CustomException;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CourseRepository extends JpaRepository<Course, Long> {

    default Course getCourse(Long id) {
        return findById(id)
            .orElseThrow(() -> new CustomException(CourseErrorCode.NOT_FOUND));
    }

    List<Course> findAllByWriterOrderByCreatedAtDesc(Member writer);

    @Query(value = "SELECT DISTINCT c.* FROM course c " +
        "JOIN course_option o ON c.id = o.course_id " +
        "WHERE (:latitude IS NULL OR ST_DWithin(c.start_point, ST_MakePoint(:longitude, :latitude)::geography, :radius)) " +
        "AND c.difficulty = :difficulty " +
        "AND (:optionsList IS NULL OR o.type = ANY(:optionsList))",
        nativeQuery = true)
    List<Course> recommendCourses(
        @Param("latitude") Double latitude,
        @Param("longitude") Double longitude,
        @Param("radius") Double radius,
        @Param("difficulty") String difficulty,
        @Param("optionsList") List<String> optionsList);

    @Query(value = "SELECT DISTINCT c.* FROM course c " +
        "JOIN course_option o ON c.id = o.course_id " +
        "WHERE (:keyword IS NULL OR c.name LIKE %:keyword% OR c.description LIKE %:keyword% OR c.city LIKE %:keyword% OR c.district LIKE %:keyword%) " +
        "AND (:latitude IS NULL OR ST_DWithin(c.start_point, ST_MakePoint(:longitude, :latitude)::geography, :radius)) " +
        "AND (COALESCE(array_length(array[:difficulties]::varchar[], 1), 0) = 0 OR c.difficulty = ANY(array[:difficulties]::varchar[])) " +
        "AND (COALESCE(array_length(array[:options]::varchar[], 1), 0) = 0 OR o.type = ANY(array[:options]::varchar[]))",
        nativeQuery = true)
    List<Course> searchCourses(
        @Param("keyword") String keyword,
        @Param("latitude") Double latitude,
        @Param("longitude") Double longitude,
        @Param("radius") Double radius,
        @Param("difficulties") List<String> difficulties,
        @Param("options") List<String> options);


}
