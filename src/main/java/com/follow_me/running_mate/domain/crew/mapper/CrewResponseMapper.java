package com.follow_me.running_mate.domain.crew.mapper;

import com.follow_me.running_mate.domain.course.dto.response.CourseResponse;
import com.follow_me.running_mate.domain.course.entity.Course;
import com.follow_me.running_mate.domain.course.entity.CourseOption;
import com.follow_me.running_mate.domain.course.entity.CoursePoint;
import com.follow_me.running_mate.domain.crew.dto.response.CrewResponse;
import com.follow_me.running_mate.domain.crew.entity.Crew;
import com.follow_me.running_mate.domain.crew.entity.CrewActivityTime;
import com.follow_me.running_mate.domain.crew.entity.CrewLocation;
import com.follow_me.running_mate.domain.enums.ActivityTimeType;
import com.follow_me.running_mate.domain.enums.CourseOptionType;
import com.follow_me.running_mate.global.common.util.FormatterUtil;
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
            CourseResponse.MyCourseListResponse myCourseListResponse
    ){
        return CrewResponse.CrewDetailResponse.builder()
                .id(crew.getId())
                .name(crew.getName())
                .detailDescription(crew.getDetailDescription())
                .profileImageUrl(crew.getProfileImageUrl())
                .activityTimes(toActivityTimeType(crewActivityTimes))
                .crewLocationInfos(toCrewLocation(crewLocations))
                .crewCourses(myCourseListResponse)
                .build();
    }

    public List<ActivityTimeType> toActivityTimeType(List<CrewActivityTime> crewActivityTimes){
        return crewActivityTimes.stream()
                .map(CrewActivityTime::getType)
                .toList();
    }

    public List<CrewResponse.CrewLocationInfo> toCrewLocation(List<CrewLocation> crewLocations){
        return crewLocations.stream()
                .map(crewLocation ->
                    CrewResponse.CrewLocationInfo.builder()
                        .city(crewLocation.getCity())
                        .district(crewLocation.getDistrict())
                        .build()
                        ).toList();
    }
}
