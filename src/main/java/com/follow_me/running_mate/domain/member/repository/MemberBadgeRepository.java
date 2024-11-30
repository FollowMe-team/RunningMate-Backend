package com.follow_me.running_mate.domain.member.repository;

import com.follow_me.running_mate.domain.enums.BadgeType;
import com.follow_me.running_mate.domain.member.entity.MemberBadge;
import com.follow_me.running_mate.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberBadgeRepository extends JpaRepository<MemberBadge, Long> {

    // 특정 회원의 배지 목록을 조회하는 메서드
    List<MemberBadge> findByMember(Member member);

    // 특정 회원과 배지 유형에 해당하는 배지 조회
    Optional<MemberBadge> findByMemberAndType(Member member, BadgeType type);
}
