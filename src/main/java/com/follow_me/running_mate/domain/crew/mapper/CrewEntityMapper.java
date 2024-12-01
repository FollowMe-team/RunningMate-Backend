package com.follow_me.running_mate.domain.crew.mapper;

import com.follow_me.running_mate.domain.course.entity.Course;
import com.follow_me.running_mate.domain.crew.dto.request.CrewRequest;
import com.follow_me.running_mate.domain.crew.entity.*;
import com.follow_me.running_mate.domain.enums.CrewScheduleApplyStatus;
import com.follow_me.running_mate.domain.enums.CrewMemberStatus;
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
                .status(CrewMemberStatus.READY) // 기본 상태 설정
                .build();
    }

    public CrewSchedule toCrewSchedule(Crew crew, Course course, CrewRequest.createSchedule request) {
        return CrewSchedule.builder()
                .crew(crew)
                .course(course)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .memberMax(request.getMemberMax())
                .build();
    }
    public CrewScheduleApply toCrewScheduleApply(CrewSchedule crewSchedule,CrewMember crewMember){
        return CrewScheduleApply.builder()
                .crewSchedule(crewSchedule)
                .crewMember(crewMember)
                .status(CrewScheduleApplyStatus.APPLY) // 상태를 참여로 설정
                .build();
    }

    public CrewCourse toCrewCourse(Crew crew, Course course) {
        return CrewCourse.builder()
                .crew(crew)
                .course(course)
                .build();
    }
}
