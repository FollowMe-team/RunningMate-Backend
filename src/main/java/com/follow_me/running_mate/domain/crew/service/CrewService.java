package com.follow_me.running_mate.domain.crew.service;

import com.follow_me.running_mate.domain.course.dto.response.CourseResponse;
import com.follow_me.running_mate.domain.course.entity.Course;
import com.follow_me.running_mate.domain.crew.dto.request.CrewRequest;
import com.follow_me.running_mate.domain.crew.dto.response.CrewResponse;
import com.follow_me.running_mate.domain.crew.entity.Crew;
import com.follow_me.running_mate.domain.crew.entity.CrewActivityTime;
import com.follow_me.running_mate.domain.crew.entity.CrewImage;
import com.follow_me.running_mate.domain.crew.entity.CrewLocation;
import com.follow_me.running_mate.domain.enums.ActivityTimeType;
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

    CrewResponse.CrewScheduleListResponse getCrewScheduleByDate(Member member,Long crewId, LocalDate date);

    CourseResponse.CourseListResponse getCrewScheduleCourses(Course course);

    List<CrewResponse.CrewMemberinfo> getMembersBySchedule(Long scheduleId);

    CrewResponse.CrewIdResponse createCrew(Member member, CrewRequest.createCrew request, MultipartFile representativeImage);

    void applyToCrew(Member member, Long crewId);

    CrewResponse.CrewScheduleIdResponse registerSchedule(Member member, Long crewId, CrewRequest.createSchedule request);
    CrewResponse.CrewScheduleApplyIdResponse applyToSchedule(Member member, Long scheduleId);
    void updateCrewMemberStatus(Member currentUser, Long memberId, String status);
    CrewResponse.UpdateCrewResponse updateCrew(Member member, Long crewId, CrewRequest.UpdateCrewRequest request);
    CrewResponse.CrewCourseListResponse getFavoriteCourses(Member member,Long crewId);
    CrewResponse.CrewCourseIdResponse addFavoriteCourse(Member member,Long crewId ,Long courseId);
    CrewResponse.ActivityImageListResponse uploadCrewImages(Long crewId, List<MultipartFile> images,Member member);
    List<CrewImage> saveImages(Crew crew, List<MultipartFile> images,Integer orderNumber);
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
