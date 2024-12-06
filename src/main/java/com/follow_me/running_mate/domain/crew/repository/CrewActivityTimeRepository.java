package com.follow_me.running_mate.domain.crew.repository;

import com.follow_me.running_mate.domain.crew.entity.Crew;
import com.follow_me.running_mate.domain.crew.entity.CrewActivityTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CrewActivityTimeRepository extends JpaRepository<CrewActivityTime,Long> {
    List<CrewActivityTime> findAllByCrew(Crew crew);

    void deleteByCrew(Crew crew);
}
