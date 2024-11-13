package com.follow_me.running_mate.domain.member.dto.response;

import com.follow_me.running_mate.domain.enums.Gender;
import com.follow_me.running_mate.domain.enums.RunningCareer;
import com.follow_me.running_mate.domain.enums.RunningGoal;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

public class MemberResponse {

    @Getter
    @AllArgsConstructor
    public static class MyProfileResponse {
        private String name;
        private String nickname;
        private Gender gender; // Gender enum
        private LocalDate birth; // LocalDate for birth
        private RunningGoal runningGoal; // RunningGoal enum
        private RunningCareer runningCareer; // RunningCareer enum
    }
    @Getter
    @AllArgsConstructor
    public static class UpdateMyProfileResponse {
        private String nickname;
        private Gender gender; // Gender enum
        private LocalDate birth; // LocalDate for birth
    }
}