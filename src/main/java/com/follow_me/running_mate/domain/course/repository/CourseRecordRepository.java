package com.follow_me.running_mate.domain.course.repository;

import com.follow_me.running_mate.domain.course.entity.CourseRecord;
import com.follow_me.running_mate.domain.member.entity.Member;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRecordRepository extends JpaRepository<CourseRecord, Long> {

    List<CourseRecord> findTop3ByRunnerOrderByStartTimeDesc(Member member);

    List<CourseRecord> findAllByRunnerAndStartTimeBetween(Member runner, LocalDateTime startOfDay, LocalDateTime endOfDay);
}
