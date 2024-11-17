package com.follow_me.running_mate.domain.member.dto.response;

import com.follow_me.running_mate.domain.enums.Gender;
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
    @Builder // 인자가 많아 생성자가 많이 필요할때 사용해 선택적으로 인자를 사용해 편하게 해주는 어노테이션
    @AllArgsConstructor // 모든 인자들을 포함하는 생성자 만들어주는 어노테이션
    @NoArgsConstructor
    public static class MyProfileResponse {
        private String name;
        private String nickname;
        private Gender gender; // Gender enum
        private LocalDate birth; // LocalDate for birth
        private RunningGoal runningGoal; // RunningGoal enum
        private RunningCareer runningCareer; // RunningCareer enum
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor // 생성자에 모든 인자를 포함하지 않아도 되는 어노테이션
    public static class UpdateMyProfileResponse {
        private String nickname;
        private Gender gender; // Gender enum
        private LocalDate birth; // LocalDate for birth
        private Point address; //주소지
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class BadgeResponse {
        private String name;
        private String description;
        private String iconUrl;
        private String criteria;    // 배지를 획득한 기준
    }
    @Getter
    @Builder
    @AllArgsConstructor
    public static class BadgeListResponse{
        private List<BadgeResponse> badges;
    }
}