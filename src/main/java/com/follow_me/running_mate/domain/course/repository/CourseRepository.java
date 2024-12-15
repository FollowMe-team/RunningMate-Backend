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
        "WHERE (:latitude IS NULL OR ST_DWithin(c.start_point, ST_MakePoint(:longitude, :latitude)::geography, :radius)) " +
        "AND c.difficulty = :difficulty " +
        "AND c.status = 'COMPLETE' " +
        "AND (COALESCE(:optionsList) IS NULL OR o.type IN (:optionsList))",
        nativeQuery = true)
    List<Course> recommendCourses(
        @Param("latitude") Double latitude,
        @Param("longitude") Double longitude,
        @Param("radius") Double radius,
        @Param("difficulty") String difficulty,
        @Param("optionsList") List<String> optionsList);

    @Query(value = "SELECT DISTINCT c.*\n" +
        "FROM course c\n" +
        "LEFT JOIN course_option o ON c.id = o.course_id\n" +
        "WHERE \n" +
        "    (:keyword IS NULL OR \n" +
        "     c.name ILIKE CONCAT('%', :keyword, '%') OR \n" +
        "     c.description ILIKE CONCAT('%', :keyword, '%') OR \n" +
        "     c.city ILIKE CONCAT('%', :keyword, '%') OR \n" +
        "     c.district ILIKE CONCAT('%', :keyword, '%')) \n" +
        "    AND (:latitude IS NOT NULL AND :longitude IS NOT NULL AND \n" +
        "         ST_DWithin(c.start_point, ST_MakePoint(:longitude, :latitude)::geography, :radius)) \n" +
        "    AND (:distance IS NULL OR \n" +
        "         (:distance = 'UNDER_3KM' AND c.distance < 3 OR \n" +
        "          :distance = 'BETWEEN_3KM_AND_5KM' AND c.distance BETWEEN 3 AND 5 OR \n" +
        "          :distance = 'BETWEEN_5KM_AND_10KM' AND c.distance BETWEEN 5 AND 10 OR \n" +
        "          :distance = 'UPPER_10KM' AND c.distance > 10)) \n" +
        "    AND (:difficulties IS NULL OR c.difficulty IN (:difficulties)) \n" +
        "    AND (:options IS NULL OR o.type IN (:options)) \n" +
        "    AND c.status = 'COMPLETE'\n" +
        "    AND c.deleted_at IS NULL;",
        nativeQuery = true)
    List<Course> searchCourses(
        @Param("keyword") String keyword,
        @Param("latitude") Double latitude,
        @Param("longitude") Double longitude,
        @Param("radius") Double radius,
        @Param("distance") String distance,
        @Param("difficulties") List<String> difficulties,
        @Param("options") List<String> options);
}
