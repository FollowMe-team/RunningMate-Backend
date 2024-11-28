package com.follow_me.running_mate.domain.crew.mapper;

import com.follow_me.running_mate.domain.course.dto.response.CourseResponse;
import com.follow_me.running_mate.domain.crew.dto.response.CrewResponse;
import com.follow_me.running_mate.domain.crew.entity.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CrewResponseMapper {
    public List<CrewResponse.MyCrewResponse> toCrewInfoResponse(List<Crew> CrewList) {
        return CrewList.stream()
                .map(crew ->
                        CrewResponse.MyCrewResponse.builder()
                                .id(crew.getLeader().getId())
                                .name(crew.getName())
                                .memberCount(crew.getMemberCount())
                                .shortDescription(crew.getShortDescription())
                                .profileImageUrl(crew.getProfileImageUrl())
                                .build())
                .toList();
    }

    public CrewResponse.CrewDetailResponse toCrewDetailInfo(
            Crew crew,
            List<CrewActivityTime> crewActivityTimes,
            List<CrewLocation> crewLocations,
            CourseResponse.CourseListResponse myCourseListResponse
    ) {
        return CrewResponse.CrewDetailResponse.builder()
                .id(crew.getId())
                .name(crew.getName())
                .detailDescription(crew.getDetailDescription())
                .profileImageUrl(crew.getProfileImageUrl())
                .crewActivityTimeList(toCrewActivityTimes(crewActivityTimes))
                .crewLocationInfos(toCrewLocation(crewLocations))
                .crewCourses(myCourseListResponse)
                .build();
    }

    public CrewResponse.UpdateCrewResponse toUpdateCrewInfo(
            Crew crew,
            List<CrewActivityTime> crewActivityTimes
    ) {
        return CrewResponse.UpdateCrewResponse.builder()
                .id(crew.getId())
                .name(crew.getName())
                .detailDescription(crew.getDetailDescription())
                .profileImageUrl(crew.getProfileImageUrl())
                .crewActivityTimeList(toCrewActivityTimes(crewActivityTimes))
                .build();
    }

    public List<CrewResponse.CrewActivityTime> toCrewActivityTimes(List<CrewActivityTime> crewActivityTimes) {
        return crewActivityTimes.stream()
                .map(crewActivityTime -> CrewResponse.CrewActivityTime.builder()
                        .activityTimes(crewActivityTime.getType()) // 타입을 리스트로 래핑
                        .startTime(crewActivityTime.getStartTime())
                        .endTime(crewActivityTime.getEndTime())
                        .build())
                .toList();
    }

    public List<CrewResponse.CrewLocationInfo> toCrewLocation(List<CrewLocation> crewLocations) {
        return crewLocations.stream()
                .map(crewLocation ->
                        CrewResponse.CrewLocationInfo.builder()
                                .city(crewLocation.getCity())
                                .district(crewLocation.getDistrict())
                                .build()
                ).toList();
    }

    public CrewResponse.CrewScheduleInfo toCrewScheduleInfo(
            CrewSchedule crewSchedules,
            CourseResponse.CourseListResponse courseInfo
    ) {
        return CrewResponse.CrewScheduleInfo.builder()
                .id(crewSchedules.getId())
                .startTime(crewSchedules.getStartTime())
                .endTime(crewSchedules.getEndTime())
                .memberCount(crewSchedules.getMemberCount())
                .memberMax(crewSchedules.getMemberMax())
                .crewCourse(courseInfo)
                .build();
    }
    public List<CrewResponse.CrewActivityImageResponse> toCrewActivityImages(
            List<CrewImage> crewImages
    ){
        return crewImages.stream()
                .map(crewImage ->
                        CrewResponse.CrewActivityImageResponse.builder()
                                .imageUrls(crewImage.getUrl())
                                .CrewActivityId(crewImage.getId())
                                .OrderNumber(crewImage.getOrderNumber())
                                .build()
                ) .toList();
    }
}
