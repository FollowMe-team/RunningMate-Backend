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

    @Query("SELECT DISTINCT c FROM Course c " +
        "JOIN c.options o " +
        "WHERE (:latitude IS NULL OR ST_DWithin(c.startPoint, ST_MakePoint(:longitude, :latitude), :radius)) " +
        "AND c.difficulty = :difficulty " +
        "AND (:optionsList IS EMPTY OR o.type IN :optionsList)")
    List<Course> recommendCourses(
        @Param("latitude") Double latitude,
        @Param("longitude") Double longitude,
        @Param("radius") Double radius,
        @Param("difficulty") Difficulty difficulty,
        @Param("optionsList") List<CourseOptionType> optionsList);

    @Query("SELECT DISTINCT c FROM Course c " +
        "JOIN c.options o " +
        "WHERE (:keyword IS NULL OR c.name LIKE %:keyword% OR c.description LIKE %:keyword% OR c.city LIKE %:keyword% OR c.district LIKE %:keyword%) " +
        "AND (:latitude IS NULL OR ST_DWithin(c.startPoint, ST_MakePoint(:longitude, :latitude), :radius)) " +
        "AND (:difficulties IS EMPTY OR c.difficulty IN :difficulties) " +
        "AND (:options IS EMPTY OR o.type IN :options)")
    List<Course> searchCourses(
        @Param("keyword") String keyword,
        @Param("latitude") Double latitude,
        @Param("longitude") Double longitude,
        @Param("radius") Double radius,
        @Param("difficulties") List<Difficulty> difficulties,
        @Param("options") List<CourseOptionType> options);
}
