package com.follow_me.running_mate.domain.crew.service;

import com.follow_me.running_mate.domain.course.dto.response.CourseResponse;
import com.follow_me.running_mate.domain.course.entity.Course;
import com.follow_me.running_mate.domain.crew.dto.request.CrewRequest;
import com.follow_me.running_mate.domain.crew.dto.response.CrewResponse;
import com.follow_me.running_mate.domain.crew.entity.Crew;
import com.follow_me.running_mate.domain.crew.entity.CrewImage;
import com.follow_me.running_mate.domain.enums.ActivityTimeType;
import com.follow_me.running_mate.domain.enums.CrewMemberStatus;
import com.follow_me.running_mate.domain.member.entity.Member;
import org.springframework.web.multipart.MultipartFile;

import java.time.YearMonth;
import java.util.List;

public interface CrewService {
    List<Crew> getCrewByCourse(Course course);

    CrewResponse.MyCrewListResponse getCrewsByMember(Member member);

    CrewResponse.CrewDetailResponse getCrewDetail(Member member,Long crewId);
    CrewResponse.CrewScheduleListResponse getCrewScheduleByDate(Member member,Long crewId, YearMonth yearMonth);
    List<CrewResponse.CrewMemberInfo> getMembersBySchedule(Long scheduleId);

    CrewResponse.CrewIdResponse createCrew(Member member, CrewRequest.createCrew request, MultipartFile representativeImage);

    void applyToCrew(Member member, Long crewId);

    CrewResponse.CrewScheduleIdResponse registerSchedule(Member member, Long crewId, CrewRequest.createSchedule request);
    CrewResponse.CrewScheduleApplyIdResponse applyToSchedule(Member member, Long scheduleId);
    void updateCrewMemberStatus(Member currentUser, Long crewId, Long memberId, CrewMemberStatus status);
    CrewResponse.CrewIdResponse updateCrew(
        Member member, Long crewId, CrewRequest.UpdateCrewRequest request, MultipartFile representativeImage);
    CrewResponse.CrewCourseListResponse getFavoriteCourses(Member member,Long crewId);
    CrewResponse.CrewCourseIdResponse addFavoriteCourse(Member member,Long crewId ,Long courseId);
    void uploadCrewImages(Member member, Long crewId, List<MultipartFile> images);
    CrewResponse.UpdateCrewSchedule updateSchedule(Member member,Long crewId, Long scheduleId, CrewRequest.createSchedule request);
    void cancelScheduleApply(Member member, Long scheduleId);
    void attendSchedule(Member member, Long scheduleId, List<Long> memberIds);
    void changeLeader(Member currentMember, Long newLeaderId);
    void cancelCrewApplication(Member member, Long crewId);
    void deleteCrew(Member member, Long crewId);
    void deleteCrewSchedule(Member member, Long scheduleId);
    void deleteFavoriteCourse(Member member, Long courseId,Long crewId);
    CrewResponse.MyCrewListResponse searchCrews(
            Member member, String keyword, String city,
            String district, List<ActivityTimeType> activityTimes
    );
    boolean canMemberJoinCrew(Member currentUser,Long crewId);
    CrewResponse.CrewSelectResponse getCrewSelectDetail(Member member, Long crewId);
}
