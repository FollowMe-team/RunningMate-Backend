package com.follow_me.running_mate.domain.crew.repository;

import com.follow_me.running_mate.domain.crew.entity.Crew;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CrewRepository extends JpaRepository<Crew,Long> {
    @Query("SELECT c FROM Crew c WHERE c.id NOT IN :myCrewIds ORDER BY c.createdAt DESC")
    List<Crew> findTop4ByIdNotInOrderByCreatedAtDesc(@Param("myCrewIds") List<Long> myCrewIds);
}
