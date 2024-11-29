package com.follow_me.running_mate.domain.member.repository;

import com.follow_me.running_mate.domain.member.entity.Member;
import com.follow_me.running_mate.domain.member.entity.MemberFootprint;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberFootprintRepository extends JpaRepository<MemberFootprint, Long> {
    List<MemberFootprint> findAllByTargetOrderByCreatedAtDesc(Member target);
}
