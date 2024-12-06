package com.follow_me.running_mate.domain.member.repository;

import com.follow_me.running_mate.domain.member.entity.Member;
import com.follow_me.running_mate.domain.member.entity.MemberFollow;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MemberFollowRepository extends JpaRepository<MemberFollow, Long> {

    @Query("SELECT f.following FROM MemberFollow f WHERE f.follower = :follower AND f.isActive = true")
    List<Member> findFollowingByFollower(Member follower);

    @Query("SELECT f.follower FROM MemberFollow f WHERE f.following = :following AND f.isActive = true")
    List<Member> findFollowerByFollowing(Member following);

    Boolean existsByFollowerAndFollowingAndIsActiveTrue(Member follower, Member following);

    Optional<MemberFollow> findByFollowerAndFollowing(Member follower, Member following);
}
