package com.follow_me.running_mate.domain.member.dto.request;

import com.follow_me.running_mate.domain.enums.Gender;
import com.follow_me.running_mate.domain.enums.RunningCareer;
import com.follow_me.running_mate.domain.enums.RunningGoal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MemberRequest {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SignUpRequest {
        private String email;
        private String password;
        private String name;
        private Gender gender;
        private LocalDate birth;
        private RunningGoal runningGoal;
        private String nickname;
        private RunningCareer runningCareer;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LoginRequest {
        private String email;
        private String password;
    }
}
