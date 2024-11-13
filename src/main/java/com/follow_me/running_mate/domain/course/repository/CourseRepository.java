package com.follow_me.running_mate.domain.course.repository;

import com.follow_me.running_mate.domain.course.entity.Course;
import com.follow_me.running_mate.domain.course.exception.CourseErrorCode;
import com.follow_me.running_mate.domain.member.entity.Member;
import com.follow_me.running_mate.global.error.exception.CustomException;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CourseRepository extends JpaRepository<Course, Long> {

    default Course getCourse(Long id) {
        // TODO: 승인 완료된 코스만 조회하도록 수정
        return findById(id)
            .orElseThrow(() -> new CustomException(CourseErrorCode.NOT_FOUND));
    }

    List<Course> findAllByWriterOrderByCreatedAtDesc(Member writer);

    @Query(value = "SELECT DISTINCT c.* FROM course c " +
        "JOIN course_option o ON c.id = o.course_id " +
        "WHERE (:latitude IS NULL OR ST_DWithin(c.start_point, ST_MakePoint(:longitude, :latitude)::geography, :radius)) " +
        "AND c.difficulty = :difficulty " +
        "AND (COALESCE(:optionsList) IS NULL OR o.type IN (:optionsList))",
        nativeQuery = true)
    List<Course> recommendCourses(
        @Param("latitude") Double latitude,
        @Param("longitude") Double longitude,
        @Param("radius") Double radius,
        @Param("difficulty") String difficulty,
        @Param("optionsList") List<String> optionsList);

    @Query(value = "SELECT DISTINCT c.* FROM course c " +
        "JOIN course_option o ON c.id = o.course_id " +
        "WHERE (:keyword IS NULL OR c.name LIKE CONCAT('%', :keyword, '%') " +
        "OR c.description LIKE CONCAT('%', :keyword, '%') " +
        "OR c.city LIKE CONCAT('%', :keyword, '%') " +
        "OR c.district LIKE CONCAT('%', :keyword, '%')) " +
        "AND (:latitude IS NULL OR ST_DWithin(c.start_point, ST_MakePoint(:longitude, :latitude)::geography, :radius)) " +
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
