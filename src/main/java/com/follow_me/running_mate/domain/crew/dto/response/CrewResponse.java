package com.follow_me.running_mate.domain.crew.dto.response;

import com.follow_me.running_mate.domain.course.dto.response.CourseResponse;
import com.follow_me.running_mate.domain.enums.ActivityTimeType;
import com.follow_me.running_mate.domain.enums.CourseImageType;
import com.follow_me.running_mate.domain.member.dto.response.MemberResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class CrewResponse {
    @Getter
    @AllArgsConstructor
    public static class CrewIdResponse {
        private Long crewId;
    }
    @Getter
    @Builder // 인자가 많아 생성자가 많이 필요할때 사용해 선택적으로 인자를 사용해 편하게 해주는 어노테이션
    @AllArgsConstructor // 모든 인자들을 포함하는 생성자 만들어주는 어노테이션
    @NoArgsConstructor
    public static class MyCrewResponse{
        private Long id;
        private String name;
        private Integer memberCount;
        private String shortDescription;
        private String profileImageUrl;
        //TODO: 크루 발자국 어떻게 처리할지 고민하기
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class MyCrewListResponse{
        List<MyCrewResponse> myCrews;
        List<MyCrewResponse> recommendedCrews;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CrewCourse {
        private Long id;
        private String imageUrl;
        private CourseImageType type;
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
        private List<CrewLocationInfo> crewLocationInfos;
        private CourseResponse.MyCourseListResponse crewCourses;
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
        private  String city;
        private String district;
    }
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CrewScheduleListResponse {
        private Long crewId;
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
        private CourseResponse.MyCourseListResponse crewCourse;
    }
    @Getter
    @Builder
    @AllArgsConstructor
    public static class CrewScheduleMemberListResponse{
        private List<MemberResponse.FollowResponse> CrewScheduleMembers;
    }
}
