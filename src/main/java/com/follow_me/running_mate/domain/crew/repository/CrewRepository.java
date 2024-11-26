package com.follow_me.running_mate.domain.crew.repository;

import com.follow_me.running_mate.domain.crew.entity.Crew;
import com.follow_me.running_mate.domain.crew.exception.CrewErrorCode;
import com.follow_me.running_mate.domain.member.entity.Member;
import com.follow_me.running_mate.global.error.exception.CustomException;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CrewRepository extends JpaRepository<Crew,Long> {
    @Query("SELECT c FROM Crew c WHERE c.id NOT IN :myCrewIds ORDER BY c.createdAt DESC")
    List<Crew> findTop4ByIdNotInOrderByCreatedAtDesc(@Param("myCrewIds") List<Long> myCrewIds);

    default Crew getCrew(Long crewId) {
        return findById(crewId)
                .orElseThrow(() -> new CustomException(CrewErrorCode.NOT_FOUND));  // 크루가 없을 경우 예외 처리
    }
    boolean existsByName(String name);
    Optional<Crew> findByLeader(Member leader);
}
