package com.follow_me.running_mate.domain.crew.dto.request;

import com.follow_me.running_mate.domain.crew.validation.annotation.UniqueCrewName;
import com.follow_me.running_mate.domain.enums.ActivityTimeType;
import com.follow_me.running_mate.domain.enums.Ranking;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public class CrewRequest {
    @Getter
    @AllArgsConstructor
    public static class createCrew {

        @NotBlank(message = "크루명을 입력해주세요.")
        @UniqueCrewName
        private String name;

        @NotBlank(message = "간단한 크루 소개를 입력해주세요.")
        private String shortDescription;

        @NotBlank(message = "크루 상세 소개를 입력해주세요.")
        private String detailDescription;

        @NotBlank(message = "오픈채팅방 링크를 입력해주세요.")
        private String openChatUrl;

        @Size(min = 1, message = "최소 1개의 활동 시간을 입력해주세요.")
        private List<ActivityTime> activityTimes;
    }
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class attendCrewSchedule {
        @NotNull(message = "출석 체크 할 인원들을 보내주세요 출석한 인원이 없다면 빈 배열을 보내주세요")
        private List<Long> memberIds;
    }

    @Getter
    @AllArgsConstructor
    public static class ActivityTime {
        private String startTime;
        private String endTime;
        @NotNull
        private ActivityTimeType type;
    }

    @Getter
    @AllArgsConstructor
    public static class CreateSchedule {
        @NotNull(message = "코스를 선택해주세요.")
        private Long courseId;

        @NotNull(message = "시작 시간을 입력해주세요.")
        @Future(message = "시작 시간은 현재 시간 이후여야 합니다.")
        private LocalDateTime startTime;

        @NotNull(message = "종료 시간을 입력해주세요.")
        @Future(message = "종료 시간은 시작 시간 이후여야 합니다.")
        private LocalDateTime endTime;

        @Min(value = 1, message = "최소 1명 이상의 인원이 필요합니다.")
        private Integer memberMax;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateCrewRequest {

        @NotBlank(message = "크루명을 입력해주세요.")
        private String name;

        @NotBlank(message = "간단한 크루 소개를 입력해주세요.")
        private String shortDescription;

        @NotBlank(message = "크루 상세 소개를 입력해주세요.")
        private String detailDescription;

        @NotBlank(message = "시/도를 선택해주세요.")
        private String city;

        @NotBlank(message = "구/군을 선택해주세요.")
        private String district;

        private Ranking ranking;

        @NotBlank(message = "오픈채팅방 링크를 입력해주세요.")
        private String openChatUrl;

        @Size(min = 1, message = "최소 1개의 활동 시간을 입력해주세요.")
        private List<ActivityTime> activityTimes;
    }
}

