package com.follow_me.running_mate.domain.member.repository;


import com.follow_me.running_mate.domain.member.entity.Member;
import com.follow_me.running_mate.domain.member.exception.MemberErrorCode;
import com.follow_me.running_mate.global.error.exception.CustomException;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    default Member getMember(Long id) {
        return findById(id)
            .orElseThrow(() ->new CustomException(MemberErrorCode.NOT_FOUND));
    }

    Optional<Member> findByEmail(String email);
    Optional<Member> findByNickname(String nickname);
    boolean existsByNickname(String nickname);
    boolean existsByEmail(String email);
}
