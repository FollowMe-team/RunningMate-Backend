package com.follow_me.running_mate.domain.member.repository;

import com.follow_me.running_mate.domain.member.entity.Member;
import com.follow_me.running_mate.domain.member.entity.MemberFollow;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MemberFollowRepository extends JpaRepository<MemberFollow, Long> {

    @Query("SELECT f.following FROM MemberFollow f WHERE f.follower = :follower AND f.isActive = true")
    List<Member> findFollowingByFollower(Member follower);

    @Query("SELECT f.follower FROM MemberFollow f WHERE f.following = :following AND f.isActive = true")
    List<Member> findFollowerByFollowing(Member following);

    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END " +
        "FROM MemberFollow f " +
        "WHERE f.follower.id = :followerId AND f.following.id = :followingId AND f.isActive = true"
    )
    boolean existsActiveFollowByFollowerIdAndFollowedId(
        @Param("followerId") Long followerId, @Param("followingId") Long followingId
    );
    Optional<MemberFollow> findByFollowerAndFollowing(Member follower, Member following);
}
