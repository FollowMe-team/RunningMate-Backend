package com.follow_me.running_mate.domain.crew.repository;

import com.follow_me.running_mate.domain.crew.entity.Crew;
import com.follow_me.running_mate.domain.crew.entity.CrewActivityTime;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CrewActivityTimeRepository extends JpaRepository<CrewActivityTime,Long> {
    List<CrewActivityTime> findAllByCrew(Crew crew);

    void deleteByCrew(Crew crew);
}
