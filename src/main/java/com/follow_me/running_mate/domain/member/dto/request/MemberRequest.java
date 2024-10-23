package com.follow_me.running_mate.domain.member.dto.request;

import com.follow_me.running_mate.domain.enums.Gender;
import com.follow_me.running_mate.domain.enums.RunningCareer;
import com.follow_me.running_mate.domain.enums.RunningGoal;
import com.follow_me.running_mate.domain.member.validation.annotation.UniqueEmail;
import com.follow_me.running_mate.domain.member.validation.annotation.UniqueNickname;
import com.follow_me.running_mate.global.validation.annotation.Password;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
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

        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        @UniqueEmail
        private String email;

        @NotBlank(message = "비밀번호는 필수입니다.")
        @Password
        private String password;

        @NotBlank(message = "이름은 필수입니다.")
        @Size(min = 2, max = 10, message = "이름은 2자 이상 10자 이하여야 합니다.")
        private String name;

        @NotNull(message = "성별은 필수입니다.")
        private Gender gender;

        @NotNull(message = "생년월일은 필수입니다.")
        @Past(message = "생년월일은 과거 날짜여야 합니다.")
        private LocalDate birth;

        private RunningGoal runningGoal;


        @NotBlank(message = "닉네임은 필수입니다.")
        @Size(min = 2, max = 10, message = "닉네임은 2자 이상 10자 이하여야 합니다.")
        @UniqueNickname
        private String nickname;

        @NotNull(message = "러닝 경력은 필수입니다.")
        private RunningCareer runningCareer;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LoginRequest {
        @NotBlank(message = "아이디 입력은 필수입니다.")
        @Email
        private String email;
        @NotBlank(message = "비밀번호 입력은 필수입니다.")
        private String password;
    }
}
