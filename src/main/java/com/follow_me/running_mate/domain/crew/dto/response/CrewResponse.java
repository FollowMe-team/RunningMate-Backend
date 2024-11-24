package com.follow_me.running_mate.domain.crew.dto.response;

import com.follow_me.running_mate.domain.course.dto.response.CourseResponse;
import com.follow_me.running_mate.domain.enums.ActivityTimeType;
import com.follow_me.running_mate.domain.enums.CourseImageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class CrewResponse {
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
        private List<ActivityTimeType> activityTimes;
        private List<CrewLocationInfo> crewLocationInfos;
        private CourseResponse.MyCourseListResponse crewCourses;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CrewLocationInfo {
        private  String city;
        private String district;
    }

}
