package com.follow_me.running_mate.domain.crew.repository;

import com.follow_me.running_mate.domain.crew.entity.Crew;
import com.follow_me.running_mate.domain.crew.entity.CrewMember;
import com.follow_me.running_mate.domain.enums.CrewMemberStatus;
import com.follow_me.running_mate.domain.member.entity.Member;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CrewMemberRepository extends JpaRepository<CrewMember, Long> {
    @Query("SELECT cm.crew FROM CrewMember cm WHERE cm.member = :member AND cm.status = :status")
    List<Crew> findCrewsByMemberAndStatus(@Param("member") Member member, @Param("status") CrewMemberStatus status);

    @Query("SELECT SUM(cm.member.footprint) FROM CrewMember cm WHERE cm.crew IN :crews AND cm.status = :status GROUP BY cm.crew")
    List<Long> sumFootprintByCrewsAndStatus(@Param("crews") List<Crew> crews, @Param("status") CrewMemberStatus status);

    @Query("SELECT SUM(cm.member.footprint) FROM CrewMember cm WHERE cm.crew = :crew AND cm.status = :status GROUP BY cm.crew")
    Long sumFootprintByCrewAndStatus(@Param("crew") Crew crew, @Param("status") CrewMemberStatus status);

    Optional<CrewMember> findByCrewAndMemberAndStatus(Crew crew, Member member , CrewMemberStatus status);
    Optional<CrewMember> findByCrewAndMember(Crew crew, Member member);

    boolean existsByCrewAndMemberAndStatus(Crew crew, Member member , CrewMemberStatus status);

    List<CrewMember> findAllByCrew(Crew crew);
}
