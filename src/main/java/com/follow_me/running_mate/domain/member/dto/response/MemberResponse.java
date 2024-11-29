package com.follow_me.running_mate.domain.member.dto.response;

import com.follow_me.running_mate.domain.enums.Gender;
import com.follow_me.running_mate.domain.enums.Ranking;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MyProfileSummaryResponse {
        private String profileImageUrl;
        private String nickname;
        private String introduce;
        private LocalDate birth;
        private Gender gender;
        private LocationInfo locationInfo;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OtherProfileResponse {
        private String profileImageUrl;
        private String nickname;
        private Ranking ranking;
        private String introduce;
        private String followerCount;
        private String followingCount;
        private Double runningDistance;
        private Long runningCount;
        private Long footPrint;
        private Boolean isSameCrew;
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
    public static class FollowInfo {
        private Long memberId;
        private String profileImageUrl;
        private String nickname;
        private Ranking ranking;
        private Long footPrint;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class FollowingListResponse {
        private List<FollowInfo> followings;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class FollowerListResponse{
        private List<FollowInfo> followers;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LocationInfo {
        private String address;
        private Double latitude;
        private Double longitude;
    }

}