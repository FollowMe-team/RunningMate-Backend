package com.follow_me.running_mate.domain.crew.service;

import com.follow_me.running_mate.domain.course.dto.response.CourseResponse;
import com.follow_me.running_mate.domain.course.entity.Course;
import com.follow_me.running_mate.domain.course.mapper.CourseResponseMapper;
import com.follow_me.running_mate.domain.course.repository.CourseRepository;
import com.follow_me.running_mate.domain.course.service.CourseService;
import com.follow_me.running_mate.domain.course.service.option.CourseOptionService;
import com.follow_me.running_mate.domain.course.service.point.CoursePointService;
import com.follow_me.running_mate.domain.course.service.review.CourseReviewService;
import com.follow_me.running_mate.domain.crew.dto.response.CrewResponse;
import com.follow_me.running_mate.domain.crew.entity.*;
import com.follow_me.running_mate.domain.crew.mapper.CrewResponseMapper;
import com.follow_me.running_mate.domain.crew.repository.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.follow_me.running_mate.domain.enums.ActivityTimeType;
import com.follow_me.running_mate.domain.enums.CrewScheduleApplyStatus;
import com.follow_me.running_mate.domain.enums.Status;
import com.follow_me.running_mate.domain.member.dto.response.MemberResponse;
import com.follow_me.running_mate.domain.member.entity.Member;
import com.follow_me.running_mate.domain.member.exception.MemberErrorCode;
import com.follow_me.running_mate.domain.member.mapper.MemberMapper;
import com.follow_me.running_mate.global.error.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CrewServiceImpl implements CrewService {

    private final CrewCourseRepository crewCourseRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final CrewResponseMapper crewResponseMapper;
    private final CrewRepository crewRepository;
    private final CrewActivityTimeRepository crewActivityTimeRepository;
    private final CrewLocationRepository crewLocationRepository;
    private final CourseResponseMapper courseResponseMapper;
    private final CourseReviewService courseReviewService;
    private final CourseOptionService courseOptionService;
    private final CoursePointService coursePointService;
    private final CrewScheduleRepository crewScheduleRepository;
    private final CrewScheduleApplyRepository crewScheduleApplyRepository;
    private final MemberMapper memberMapper;

    @Override
    public List<Crew> getCrewByCourse(Course course) {
        return crewCourseRepository.findDistinctCrewByCourse(course);
    }
    @Override
    public CrewResponse.MyCrewListResponse getCrewsByMember(Member member){
        List<Crew> myCrewMembers = crewMemberRepository.findCrewsByMemberAndStatus(member, Status.COMPLETE);
        List<Long> myCrewIds = myCrewMembers.stream()
                .map(Crew::getId) // Crew 객체에서 id 추출
                .toList();
        List<Crew> recommendedCrews = crewRepository.findTop4ByIdNotInOrderByCreatedAtDesc(myCrewIds);
        return new CrewResponse.MyCrewListResponse(crewResponseMapper.toCrewInfoResponse(myCrewMembers),crewResponseMapper.toCrewInfoResponse(recommendedCrews));
    }

    @Override
    public CrewResponse.CrewDetailResponse getCrewDetail(Long crewId){
        Crew crew = crewRepository.getCrew(crewId);

        return crewResponseMapper.toCrewDetailInfo(
                crew,
                getCrewActivityTime(crew),
                getCrewLocationInfo(crew),
                this.getCrewCourses(crew)
        );
    }

    @Override
    public List<CrewActivityTime> getCrewActivityTime(Crew crew){
        return crewActivityTimeRepository.findAllByCrew(crew);
    }

    @Override
    public List<CrewLocation> getCrewLocationInfo(Crew crew){
        return crewLocationRepository.findAllByCrew(crew);
    }

    @Override
    @Transactional(readOnly = true)
    public CourseResponse.MyCourseListResponse getCrewCourses(Crew crew) {

        List<Course> myCourses = crewCourseRepository.findTop3CoursesByCrewOrderByCreatedAtDesc(crew);

        List<CourseResponse.MyCourseInfo> courses = myCourses.stream().map(course ->
                courseResponseMapper.toCrewCourseInfo(
                        course,
                        courseReviewService.getAverageRating(course),
                        course.getRunningCount(),
                        courseOptionService.getCourseOptions(course),
                        coursePointService.getCoursePoints(course)
                )).toList();

        return new CourseResponse.MyCourseListResponse(courses);
    }
    @Override
    @Transactional(readOnly = true)
    public CrewResponse.CrewScheduleListResponse getCrewScheduleByDate(Long crewId, LocalDate date) {

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);
        Crew crew = crewRepository.getCrew(crewId);
        List<CrewSchedule> crewSchedules = crewScheduleRepository.findByCrewAndStartTimeBetween(crew, startOfDay,endOfDay);

        List<CrewResponse.CrewScheduleInfo> scheduleInfos = crewSchedules.stream().map(schedule ->
            crewResponseMapper.toCrewScheduleInfo(
                    schedule,
                    this.getCrewScheduleCourses(schedule.getCourse())
            )).toList();

        return CrewResponse.CrewScheduleListResponse.builder()
                .crewId(crew.getId())
                .crewSchedule(scheduleInfos)
                .build();
    }
    @Override
    @Transactional(readOnly = true)
    public CourseResponse.MyCourseListResponse getCrewScheduleCourses(Course course) {

        CourseResponse.MyCourseInfo courseInfo = courseResponseMapper.toCrewCourseInfo(
                        course,
                        courseReviewService.getAverageRating(course),
                        course.getRunningCount(),
                        courseOptionService.getCourseOptions(course),
                        coursePointService.getCoursePoints(course));
        return new CourseResponse.MyCourseListResponse(List.of(courseInfo));
    }
    @Override
    @Transactional(readOnly = true)
    public List<MemberResponse.FollowResponse> getMembersBySchedule(Long scheduleId){
        List<CrewMember> crewMembers = crewScheduleApplyRepository.findAllCrewMembersByScheduleIdAndStatus(scheduleId, CrewScheduleApplyStatus.PARTICIPATE);
        return crewMembers.stream()
                .map(crewMember -> memberMapper.toFollowResponse(crewMember.getMember()))
                .toList();
    }
}
