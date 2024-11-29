package com.follow_me.running_mate.domain.member.entity;

import com.follow_me.running_mate.domain.enums.Gender;
import com.follow_me.running_mate.domain.enums.Ranking;
import com.follow_me.running_mate.domain.enums.Role;
import com.follow_me.running_mate.domain.enums.RunningCareer;
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
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.SQLRestriction;
import org.locationtech.jts.geom.Point;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@SQLRestriction("deleted_at is null")
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

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
    private RunningCareer runningCareer;

    @Enumerated(EnumType.STRING)
    private Ranking ranking;

    @Column(nullable = false)
    @Builder.Default
    private Integer followerCount = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer followingCount = 0;

    @Column(nullable = false)
    @Builder.Default
    private Long footprint = 500L;

    private String introduce;

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
    // 프로필 변경
    public void updateProfile(String nickname, Gender gender, LocalDate birth) {
        this.nickname = nickname;
        this.gender = gender;
        this.birth = birth;
    }
    //비밀번호 변경
    public void changePassword(String encodedNewPassword) {
        this.password = encodedNewPassword;
    }

}
