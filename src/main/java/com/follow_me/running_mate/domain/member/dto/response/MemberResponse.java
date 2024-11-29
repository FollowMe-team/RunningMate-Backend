package com.follow_me.running_mate.domain.member.dto.response;

import com.follow_me.running_mate.domain.enums.Gender;
import com.follow_me.running_mate.domain.enums.Ranking;
import com.follow_me.running_mate.domain.enums.RunningCareer;
import com.follow_me.running_mate.domain.enums.RunningGoal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class MemberResponse {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MyProfileResponse {
        private String profileImageUrl;
        private String nickname;
        private Ranking ranking;
        private String introduce;
        private String followerCount;
        private String followingCount;
        private String name;
        private Gender gender;
        private LocalDate birth;
        private String address;
        private Double runningDistance;
        private Long runningCount;
        private Long footPrint;
    }

    @Getter
    @AllArgsConstructor
    public static class UpdateMyProfileResponse {
        private Long memberId;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class BadgeResponse {
        private String name;
        private String description;
        private String iconUrl;
        private String criteria;
        private Boolean isAcquired;
    }
    @Getter
    @Builder
    @AllArgsConstructor
    public static class BadgeListResponse{
        private List<BadgeResponse> badges;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class FollowResponse {
        private String nickname;
        private String iconUrl;
        private Long footPrint;// 배지를 획득한 기준
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class FollowListResponse{
        private List<FollowResponse> Follows;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class FollowerListResponse{
        private List<FollowResponse> Followers;
    }
}