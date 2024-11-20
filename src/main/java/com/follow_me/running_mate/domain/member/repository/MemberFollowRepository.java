package com.follow_me.running_mate.domain.member.repository;

import com.follow_me.running_mate.domain.member.entity.Member;
import com.follow_me.running_mate.domain.member.entity.MemberFollow;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MemberFollowRepository extends JpaRepository<MemberFollow, Long> {
    // is_active가 true인 경우만 포함하여 followerId로 팔로우된 사용자들의 목록 조회
    @Query("SELECT f.followed FROM MemberFollow f WHERE f.follower = :followerId AND f.isActive = true")
    List<Member> findActiveFollowedsByFollower(Member followerId);

    // is_active가 true인 경우만 포함하여 followedId로 팔로우된 사용자들의 목록 조회
    @Query("SELECT f.follower FROM MemberFollow f WHERE f.followed = :followedId AND f.isActive = true")
    List<Member> findActiveFollowersByFollowed(Member followedId);

    // is_active가 true인 특정 사용자와 특정 사용자의 팔로우 관계 여부 확인
    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM MemberFollow f WHERE f.follower.id = :followerId AND f.followed.id = :followedId AND f.isActive = true")
    boolean existsActiveFollowByFollowerIdAndFollowedId(@Param("followerId") Long followerId, @Param("followedId") Long followedId);
}
