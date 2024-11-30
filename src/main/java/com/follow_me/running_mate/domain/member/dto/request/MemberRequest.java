package com.follow_me.running_mate.domain.member.dto.request;

import com.follow_me.running_mate.domain.course.dto.request.CourseRequest;
import com.follow_me.running_mate.domain.enums.FootprintType;
import com.follow_me.running_mate.domain.enums.Gender;
import com.follow_me.running_mate.domain.enums.RunningCareer;
import com.follow_me.running_mate.domain.enums.WithdrawType;
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

        @NotBlank(message = "닉네임은 필수입니다.")
        @Size(min = 2, max = 10, message = "닉네임은 2자 이상 10자 이하여야 합니다.")
        @UniqueNickname
        private String nickname;

        private String introduce;

        @NotNull(message = "주소는 필수입니다.")
        private LocationInfo locationInfo;

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

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UpdateProfileRequest {

        @NotNull(message = "성별은 필수입니다.")
        private Gender gender;

        @NotBlank(message = "닉네임은 필수입니다.")
        @Size(min = 2, max = 10, message = "닉네임은 2자 이상 10자 이하여야 합니다.")
        private String nickname;

        @NotNull(message = "생년월일은 필수입니다.")
        @Past(message = "생년월일은 과거 날짜여야 합니다.")
        private LocalDate birth;

        private String introduce;

        @NotNull(message = "주소는 필수입니다.")
        private LocationInfo locationInfo;
    }
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChangePasswordRequest {
        @NotBlank(message = "현재 비밀번호는 필수 입력 항목입니다.")
        private String currentPassword;

        @NotBlank(message = "새 비밀번호는 필수 입력 항목입니다.")
        @Password
        private String newPassword;

    }

    @Getter
    @AllArgsConstructor
    public static class LocationInfo implements CourseRequest.GeoPoint {
        @NotBlank(message = "주소는 필수입니다.")
        private String address;
        @NotNull(message = "위도는 필수입니다.")
        private Double latitude;
        @NotNull(message = "경도는 필수입니다.")
        private Double longitude;
    }

    @Getter
    @AllArgsConstructor
    public static class FootprintRequest {
        @NotBlank(message = "평가 사유는 필수입니다.")
        private String content;
        @NotNull(message = "평가 타입은 필수입니다.")
        private FootprintType type;
        @NotNull(message = "익명 여부는 필수입니다.")
        private Boolean isAnonymous;
    }

    @Getter
    @AllArgsConstructor
    public static class WithdrawRequest {
        @NotNull(message = "탈퇴 타입은 필수입니다.")
        private WithdrawType type;
        private String reason;
    }
}
