package com.follow_me.running_mate.domain.crew.repository;

import com.follow_me.running_mate.domain.course.entity.Course;
import com.follow_me.running_mate.domain.crew.entity.Crew;
import com.follow_me.running_mate.domain.crew.entity.CrewCourse;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CrewCourseRepository extends JpaRepository<CrewCourse, Long> {

    // 특정 코스를 사용하는 중복 없는 크루 목록 조회
    @Query("SELECT DISTINCT cc.crew FROM CrewCourse cc WHERE cc.course = :course")
    List<Crew> findDistinctCrewByCourse(@Param("course") Course course);
    @Query("SELECT cc.course FROM CrewCourse cc WHERE cc.crew = :crew ORDER BY cc.createdAt DESC")
    List<Course> findTop3CoursesByCrewOrderByCreatedAtDesc(@Param("crew") Crew crew);
    List<CrewCourse> findAllByCrew(Crew crew);
    boolean existsByCrewIdAndCourseId(Long crewId, Long courseId);
}
