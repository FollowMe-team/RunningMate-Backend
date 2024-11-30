package com.follow_me.running_mate.domain.member.repository;

import com.follow_me.running_mate.domain.member.entity.MemberWithdraw;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberWithdrawRepository extends JpaRepository<MemberWithdraw, Long> {
}
