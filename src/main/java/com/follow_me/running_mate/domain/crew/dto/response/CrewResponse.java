package com.follow_me.running_mate.domain.crew.dto.response;

import com.follow_me.running_mate.domain.course.dto.response.CourseResponse;
import com.follow_me.running_mate.domain.enums.ActivityTimeType;
import com.follow_me.running_mate.domain.enums.Ranking;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class CrewResponse {
    @Getter
    @AllArgsConstructor
    public static class CrewIdResponse {
        private Long crewId;
    }

    @Getter
    @AllArgsConstructor
    public static class CrewScheduleIdResponse {
        private Long scheduleId;
    }

    @Getter
    @AllArgsConstructor
    public static class CrewCourseIdResponse {
        private Long crewCourseId;
    }

    @Getter
    @AllArgsConstructor
    public static class CrewScheduleApplyIdResponse {
        private Long ScheduleApplyId;
    }
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CrewMemberResponse {
        private Long memberId;
        private String status;
    }
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CrewApplyResponse{
        private Long memberId;
        private String status;
    }


    @Getter
    @Builder // 인자가 많아 생성자가 많이 필요할때 사용해 선택적으로 인자를 사용해 편하게 해주는 어노테이션
    @AllArgsConstructor // 모든 인자들을 포함하는 생성자 만들어주는 어노테이션
    @NoArgsConstructor
    public static class MyCrewResponse {
        private Long id;
        private String name;
        private Integer memberCount;
        private String shortDescription;
        private String profileImageUrl;
        private Long footprintAverage;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class MyCrewListResponse {
        List<MyCrewResponse> myCrews;
    }

    @Getter
    @AllArgsConstructor
    public static class DuplicateCheckResponse {
        private Boolean isDuplicated;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class recommendedCrewListResponse {
        List<MyCrewResponse> recommendedCrews;
    }

    @Getter
    @AllArgsConstructor
    public static class CrewCourseListResponse {
        private Long crewId;
        private Boolean IsCrewLeader;
        private List<CourseResponse.SummaryInfo> courses;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CrewDetailResponse {
        private Long id;
        private String name;
        private String detailDescription;
        private String profileImageUrl;
        private List<CrewActivityTime> crewActivityTimeList;
        private CrewLocationInfo crewLocationInfos;
        private CourseResponse.CourseListResponse crewCourses;
        private Ranking ranking;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CrewSelectResponse {
        private Long id;
        private Boolean IsCrewLeader;
        private String name;
        private String openChatUrl;
        private List<CrewImageInfo> images;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CrewImageInfo {
        private Integer orderNumber;
        private String openChatUrl;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CrewActivityTime {
        private ActivityTimeType activityTimes;
        private String startTime;
        private String endTime;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CrewLocationInfo {
        private String city;
        private String district;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CrewScheduleListResponse {
        private Long crewId;
        private Boolean IsCrewLeader;
        private List<CrewScheduleInfo> crewSchedule;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CrewScheduleInfo {
        private Long id;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private Integer memberCount;
        private Integer memberMax;
        private CourseResponse.SummaryInfo crewCourse;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class CrewScheduleMemberListResponse {
        private List<CrewMemberInfo> CrewScheduleMembers;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class CrewMemberInfo {
        private Long memberId;
        private String profileImageUrl;
        private String nickname;
        private Ranking ranking;
        private Long footPrint;
    }

    @Getter
    @AllArgsConstructor
    public static class CheckJoinCrewResponse {
        private Boolean isAvailable;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CrewUpdateResponse {
        private Long id;
        private String name;
        private String shortDescription;
        private String detailDescription;
        private String profileImageUrl;
        private Ranking ranking;
        private List<CrewActivityTime> crewActivityTimeList;
        private CrewLocationInfo crewLocationInfos;
        private String openChatUrl;
    }
}
