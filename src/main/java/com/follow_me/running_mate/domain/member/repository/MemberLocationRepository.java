package com.follow_me.running_mate.domain.member.repository;

import com.follow_me.running_mate.domain.member.entity.MemberLocation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberLocationRepository extends JpaRepository<MemberLocation, Long> {
}
