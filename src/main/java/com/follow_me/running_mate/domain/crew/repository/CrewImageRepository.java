package com.follow_me.running_mate.domain.crew.repository;

import com.follow_me.running_mate.domain.crew.entity.Crew;
import com.follow_me.running_mate.domain.crew.entity.CrewImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CrewImageRepository extends JpaRepository<CrewImage,Long> {
    int countByCrew(Crew crew);
    List<CrewImage> findAllByCrew(Crew crew);
    List<CrewImage> findAllByCrewOrderByOrderNumberAsc(Crew crew);
}
