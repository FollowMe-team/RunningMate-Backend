package com.follow_me.running_mate.domain.member.repository;


import com.follow_me.running_mate.domain.member.entity.Member;
import com.follow_me.running_mate.domain.member.exception.MemberErrorCode;
import com.follow_me.running_mate.global.error.exception.CustomException;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    default Member getMember(Long id) {
        return findById(id)
            .orElseThrow(() ->new CustomException(MemberErrorCode.NOT_FOUND));
    }

    Optional<Member> findByEmail(String email);
    Optional<Member> findByNickname(String nickname);
    @Query(value = "SELECT EXISTS(SELECT 1 FROM member WHERE nickname = :nickname)", nativeQuery = true)
    boolean existsByNickname(@Param("nickname") String nickname);
    @Query(value = "SELECT EXISTS(SELECT 1 FROM member WHERE email = :email)", nativeQuery = true)
    boolean existsByEmail(@Param("email") String email);
}
