package com.follow_me.running_mate.domain.crew.repository;

import com.follow_me.running_mate.domain.crew.entity.Crew;
import com.follow_me.running_mate.domain.crew.entity.CrewLocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CrewLocationRepository extends JpaRepository<CrewLocation,Long> {
    List<CrewLocation> findAllByCrew(Crew crew);

    Optional<CrewLocation> findByCrew(Crew crew);
}
