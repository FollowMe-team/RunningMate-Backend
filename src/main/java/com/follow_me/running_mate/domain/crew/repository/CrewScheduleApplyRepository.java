package com.follow_me.running_mate.domain.crew.repository;

import com.follow_me.running_mate.domain.crew.entity.CrewMember;
import com.follow_me.running_mate.domain.crew.entity.CrewSchedule;
import com.follow_me.running_mate.domain.crew.entity.CrewScheduleApply;
import com.follow_me.running_mate.domain.enums.CrewScheduleApplyStatus;
import com.follow_me.running_mate.domain.member.entity.Member;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CrewScheduleApplyRepository extends JpaRepository<CrewScheduleApply, Long> {

    @Query("SELECT c.crewMember FROM CrewScheduleApply c " +
            "JOIN c.crewSchedule cs " +
            "WHERE cs.id = :scheduleId AND c.status = :status")
    List<CrewMember> findAllCrewMembersByScheduleIdAndStatus(
            @Param("scheduleId") Long scheduleId,
            @Param("status") CrewScheduleApplyStatus status);

    boolean existsByCrewScheduleAndCrewMember(CrewSchedule crewSchedule, CrewMember crewMember);

    Optional<CrewScheduleApply> findByCrewScheduleAndCrewMember(CrewSchedule crewSchedule, CrewMember crewMember);
}

