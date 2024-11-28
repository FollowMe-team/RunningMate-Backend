package com.follow_me.running_mate.domain.crew.service;

import com.follow_me.running_mate.domain.course.dto.response.CourseResponse;
import com.follow_me.running_mate.domain.course.entity.Course;
import com.follow_me.running_mate.domain.crew.dto.request.CrewRequest;
import com.follow_me.running_mate.domain.crew.dto.response.CrewResponse;
import com.follow_me.running_mate.domain.crew.entity.Crew;
import com.follow_me.running_mate.domain.crew.entity.CrewActivityTime;
import com.follow_me.running_mate.domain.crew.entity.CrewLocation;
import com.follow_me.running_mate.domain.enums.Status;
import com.follow_me.running_mate.domain.member.dto.response.MemberResponse;
import com.follow_me.running_mate.domain.member.entity.Member;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public interface CrewService {
    List<Crew> getCrewByCourse(Course course);

    CrewResponse.MyCrewListResponse getCrewsByMember(Member member);

    CrewResponse.CrewDetailResponse getCrewDetail(Long crewId);

    List<CrewActivityTime> getCrewActivityTime(Crew crew);

    List<CrewLocation> getCrewLocationInfo(Crew crew);

    CourseResponse.CourseListResponse getCrewCourses(Crew crew);

    CrewResponse.CrewScheduleListResponse getCrewScheduleByDate(Long crewId, LocalDate date);

    CourseResponse.CourseListResponse getCrewScheduleCourses(Course course);

    List<MemberResponse.FollowResponse> getMembersBySchedule(Long scheduleId);

    CrewResponse.CrewIdResponse createCrew(Member member, CrewRequest.createCrew request, MultipartFile representativeImage);

    void applyToCrew(Member member, Long crewId);

    CrewResponse.CrewScheduleIdResponse registerSchedule(Member member, Long crewId, CrewRequest.createSchedule request);
    CrewResponse.CrewScheduleApplyIdResponse applyToSchedule(Member member, Long scheduleId);
    void updateCrewMemberStatus(Member currentUser, Long memberId, String status);
    CrewResponse.UpdateCrewResponse updateCrew(Member member, Long crewId, CrewRequest.UpdateCrewRequest request);
}
