package com.follow_me.running_mate.domain.member.entity;

import com.follow_me.running_mate.domain.enums.Gender;
import com.follow_me.running_mate.domain.enums.Ranking;
import com.follow_me.running_mate.domain.enums.RunningGoal;
import com.follow_me.running_mate.global.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;
import org.locationtech.jts.geom.Point;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("deleted_at is null")
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    private String nickname;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(nullable = false)
    private LocalDate birth;

    private String profileImageUrl;

    @Column(columnDefinition = "geography(Point, 4326)")
    private Point currentLocation;

    @Column(nullable = false)
    @Builder.Default()
    private Integer averageSpeed = 0;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RunningGoal runningGoal;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Ranking ranking;

    @Column(nullable = false)
    @Builder.Default
    private Integer followerCount = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer followingCount = 0;

    // 팔로워 수 증가
    public void incrementFollowerCount() {
        this.followerCount++;
    }

    // 팔로워 수 감소
    public void decrementFollowerCount() {
        if (this.followerCount > 0) {
            this.followerCount--;
        }
    }

    // 팔로잉 수 증가
    public void incrementFollowingCount() {
        this.followingCount++;
    }

    // 팔로잉 수 감소
    public void decrementFollowingCount() {
        if (this.followingCount > 0) {
            this.followingCount--;
        }
    }
}
