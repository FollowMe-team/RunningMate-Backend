package com.follow_me.running_mate.domain.crew.repository;

import com.follow_me.running_mate.domain.crew.entity.Crew;
import com.follow_me.running_mate.domain.crew.entity.CrewSchedule;
import com.follow_me.running_mate.domain.crew.exception.CrewErrorCode;
import com.follow_me.running_mate.global.error.exception.CustomException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface CrewScheduleRepository extends JpaRepository<CrewSchedule, Long> {

    default CrewSchedule getCrewSchedule(Long id) {
        return findById(id)
            .orElseThrow(() -> new CustomException(CrewErrorCode.NOT_FOUND_SCHEDULE));
    }

    List<CrewSchedule> findByCrewAndStartTimeBetween(Crew crew, LocalDateTime start, LocalDateTime end);
    boolean existsByCrewAndStartTimeBeforeAndEndTimeAfter(Crew crew, LocalDateTime endTime, LocalDateTime startTime);
    List<CrewSchedule> findAllByCrew(Crew crew);
}
