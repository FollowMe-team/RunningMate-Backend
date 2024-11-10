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
        private Long id;
        private String email;
        private String name;
        private String nickname;
        private Gender gender; // Gender enum
        private LocalDate birth; // LocalDate for birth
        private RunningGoal runningGoal; // RunningGoal enum
        private RunningCareer runningCareer; // RunningCareer enum
        // 필요에 따라 추가 필드들을 정의하세요
    }
}