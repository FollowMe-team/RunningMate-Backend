package com.follow_me.running_mate.domain.crew.mapper;

import com.follow_me.running_mate.domain.crew.dto.request.CrewRequest;
import com.follow_me.running_mate.domain.crew.entity.Crew;
import com.follow_me.running_mate.domain.crew.entity.CrewActivityTime;
import com.follow_me.running_mate.domain.crew.entity.CrewMember;
import com.follow_me.running_mate.domain.enums.Status;
import com.follow_me.running_mate.domain.member.entity.Member;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CrewEntityMapper {
    public Crew toCrew(Member leader, CrewRequest.createCrew request, String defaultProfileImageUrl) {
        return Crew.builder()
                .leader(leader)
                .name(request.getName())
                .shortDescription(request.getShortDescription())
                .detailDescription(request.getDetailDescription())
                .openChatUrl(request.getOpenChatUrl())
                .profileImageUrl(defaultProfileImageUrl)
                .build();
    }

    public List<CrewActivityTime> toCrewActivityTimes(Crew crew, List<CrewRequest.ActivityTime> activityTimes) {
        return activityTimes.stream()
                .map(activity -> CrewActivityTime.builder()
                        .crew(crew)
                        .startTime(activity.getStartTime())
                        .endTime(activity.getEndTime())
                        .type(activity.getType())
                        .build())
                .toList();
    }
    public CrewMember toCrewMember(Crew crew, Member member) {
        return CrewMember.builder()
                .crew(crew)
                .member(member)
                .status(Status.READY) // 기본 상태 설정
                .build();
    }
}
