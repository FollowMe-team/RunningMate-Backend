package com.follow_me.running_mate.domain.crew.mapper;

import com.follow_me.running_mate.domain.course.dto.response.CourseResponse;
import com.follow_me.running_mate.domain.crew.dto.response.CrewResponse;
import com.follow_me.running_mate.domain.crew.entity.*;
import com.follow_me.running_mate.domain.member.entity.Member;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class CrewResponseMapper {
    public List<CrewResponse.MyCrewResponse> toCrewInfoResponse(List<Crew> CrewList , List<Long>SumFoot) {
        AtomicInteger index = new AtomicInteger(0);
        return CrewList.stream()
                .map(crew ->
                        CrewResponse.MyCrewResponse.builder()
                                .id(crew.getId())
                                .name(crew.getName())
                                .memberCount(crew.getMemberCount())
                                .shortDescription(crew.getShortDescription())
                                .profileImageUrl(crew.getProfileImageUrl())
                                .footprintAverage(SumFoot.get(index.getAndIncrement())/crew.getMemberCount())
                                .build())
                .toList();
    }

    public List<CrewResponse.CrewApplyMemberInfo> toCrewApplyMemberInfo(List<CrewMember> CrewMembers) {
        return CrewMembers.stream()
                .map(crewMember -> CrewResponse.CrewApplyMemberInfo.builder()
                        .memberId(crewMember.getMember().getId())
                        .nickname(crewMember.getMember().getNickname())
                        .profileImageUrl(crewMember.getMember().getProfileImageUrl())
                        .footPrint(crewMember.getMember().getFootprint())
                        .ranking(crewMember.getMember().getRanking())
                        .status(crewMember.getStatus().getToKorean())
                        .build())
                .toList();
    }

    public CrewResponse.CrewDetailResponse toCrewDetailInfo(
            Crew crew,
            List<CrewActivityTime> crewActivityTimes,
            CrewLocation crewLocations,
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
                .ranking(crew.getRanking())
                .build();
    }

    public CrewResponse.CrewUpdateResponse toCrewUpdateResponse(
            Crew crew,
            List<CrewActivityTime> crewActivityTimes,
            CrewLocation crewLocations
    ) {
        return CrewResponse.CrewUpdateResponse.builder()
                .id(crew.getId())
                .name(crew.getName())
                .shortDescription(crew.getShortDescription())
                .detailDescription(crew.getDetailDescription())
                .profileImageUrl(crew.getProfileImageUrl())
                .ranking(crew.getRanking())
                .crewActivityTimeList(toCrewActivityTimes(crewActivityTimes))
                .crewLocationInfos(toCrewLocation(crewLocations))
                .openChatUrl(crew.getOpenChatUrl())
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

    public CrewResponse.CrewLocationInfo toCrewLocation(CrewLocation crewLocation) {
        return CrewResponse.CrewLocationInfo.builder()
                .city(crewLocation.getCity())
                .district(crewLocation.getDistrict())
                .build();
    }

    public CrewResponse.CrewScheduleInfo toCrewScheduleInfo(
            CrewSchedule crewSchedules,
            CourseResponse.SummaryInfo courseInfo
    ) {
        return CrewResponse.CrewScheduleInfo.builder()
                .id(crewSchedules.getId())
                .startTime(crewSchedules.getStartTime())
                .endTime(crewSchedules.getEndTime())
                .memberCount(crewSchedules.getMemberCount())
                .memberMax(crewSchedules.getMemberMax())
                .meetingPlace(crewSchedules.getMeetingPlace())
                .crewCourse(courseInfo)
                .build();
    }

    public CrewResponse.CrewMemberInfo toCrewMemberInfo(Member member){
        return CrewResponse.CrewMemberInfo.builder()
                .memberId(member.getId())
                .profileImageUrl(member.getProfileImageUrl())
                .nickname(member.getNickname())
                .footPrint(member.getFootprint())
                .ranking(member.getRanking())
                .build();
    }

    public CrewResponse.CrewSelectResponse toCrewSelectResponse(Crew crew, List<CrewImage> crewImages, boolean isCrewLeader) {
        return CrewResponse.CrewSelectResponse.builder()
                .id(crew.getId())
                .name(crew.getName())
                .openChatUrl(crew.getOpenChatUrl())
                .images(crewImages.stream()
                        .map(crewImage -> CrewResponse.CrewImageInfo.builder()
                                .openChatUrl(crewImage.getUrl())
                                .orderNumber(crewImage.getOrderNumber())
                                .build())
                        .toList())
                .IsCrewLeader(isCrewLeader)
                .build();
    }
}
