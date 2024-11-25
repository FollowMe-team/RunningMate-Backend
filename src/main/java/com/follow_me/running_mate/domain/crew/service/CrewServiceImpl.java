package com.follow_me.running_mate.domain.crew.service;

import com.follow_me.running_mate.domain.course.dto.response.CourseResponse;
import com.follow_me.running_mate.domain.course.entity.Course;
import com.follow_me.running_mate.domain.course.exception.CourseErrorCode;
import com.follow_me.running_mate.domain.course.mapper.CourseResponseMapper;
import com.follow_me.running_mate.domain.course.repository.CourseRepository;
import com.follow_me.running_mate.domain.course.service.option.CourseOptionService;
import com.follow_me.running_mate.domain.course.service.point.CoursePointService;
import com.follow_me.running_mate.domain.course.service.review.CourseReviewService;
import com.follow_me.running_mate.domain.crew.dto.request.CrewRequest;
import com.follow_me.running_mate.domain.crew.dto.response.CrewResponse;
import com.follow_me.running_mate.domain.crew.entity.*;
import com.follow_me.running_mate.domain.crew.exception.CrewErrorCode;
import com.follow_me.running_mate.domain.crew.mapper.CrewEntityMapper;
import com.follow_me.running_mate.domain.crew.mapper.CrewResponseMapper;
import com.follow_me.running_mate.domain.crew.repository.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.follow_me.running_mate.domain.enums.CrewScheduleApplyStatus;
import com.follow_me.running_mate.domain.enums.Status;
import com.follow_me.running_mate.domain.member.dto.response.MemberResponse;
import com.follow_me.running_mate.domain.member.entity.Member;
import com.follow_me.running_mate.domain.member.mapper.MemberMapper;
import com.follow_me.running_mate.global.common.service.S3ImageService;
import com.follow_me.running_mate.global.error.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
    private final CrewEntityMapper crewEntityMapper;
    private final S3ImageService s3ImageService;
    private final CourseRepository courseRepository;

    @Override
    public List<Crew> getCrewByCourse(Course course) {
        return crewCourseRepository.findDistinctCrewByCourse(course);
    }

    @Override
    public CrewResponse.MyCrewListResponse getCrewsByMember(Member member) {
        List<Crew> myCrewMembers = crewMemberRepository.findCrewsByMemberAndStatus(member, Status.COMPLETE);
        List<Long> myCrewIds = myCrewMembers.stream()
                .map(Crew::getId) // Crew 객체에서 id 추출
                .toList();
        List<Crew> recommendedCrews = crewRepository.findTop4ByIdNotInOrderByCreatedAtDesc(myCrewIds);
        return new CrewResponse.MyCrewListResponse(crewResponseMapper.toCrewInfoResponse(myCrewMembers), crewResponseMapper.toCrewInfoResponse(recommendedCrews));
    }

    @Override
    public CrewResponse.CrewDetailResponse getCrewDetail(Long crewId) {
        Crew crew = crewRepository.getCrew(crewId);

        return crewResponseMapper.toCrewDetailInfo(
                crew,
                getCrewActivityTime(crew),
                getCrewLocationInfo(crew),
                this.getCrewCourses(crew)
        );
    }

    @Override
    public List<CrewActivityTime> getCrewActivityTime(Crew crew) {
        return crewActivityTimeRepository.findAllByCrew(crew);
    }

    @Override
    public List<CrewLocation> getCrewLocationInfo(Crew crew) {
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
        List<CrewSchedule> crewSchedules = crewScheduleRepository.findByCrewAndStartTimeBetween(crew, startOfDay, endOfDay);

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
    public List<MemberResponse.FollowResponse> getMembersBySchedule(Long scheduleId) {
        List<CrewMember> crewMembers = crewScheduleApplyRepository.findAllCrewMembersByScheduleIdAndStatus(scheduleId, CrewScheduleApplyStatus.PARTICIPATE);
        return crewMembers.stream()
                .map(crewMember -> memberMapper.toFollowResponse(crewMember.getMember()))
                .toList();
    }

    @Override
    @Transactional
    public CrewResponse.CrewIdResponse createCrew(Member leader, CrewRequest.createCrew request, MultipartFile representativeImage) {
        // Crew 생성
        Crew crew;
        if (representativeImage != null) {
            String imageUrl = s3ImageService.upload(representativeImage);
            crew = crewEntityMapper.toCrew(leader, request, imageUrl);
        } else {
            crew = crewEntityMapper.toCrew(leader, request, "default-image");
        }
        crewRepository.save(crew);

        // CrewActivityTime 생성 및 저장
        List<CrewActivityTime> activityTimes = crewEntityMapper.toCrewActivityTimes(crew, request.getActivityTimes());
        crewActivityTimeRepository.saveAll(activityTimes);

        return new CrewResponse.CrewIdResponse(crew.getId());
    }

    @Override
    @Transactional
    public void applyToCrew(Member member, Long crewId) {
        // 크루 존재 여부 확인
        Crew crew = crewRepository.findById(crewId)
                .orElseThrow(() -> new CustomException(CrewErrorCode.NOT_FOUND));

        // 이미 신청했는지 확인
        boolean isAlreadyApplied = crewMemberRepository.existsByCrewAndMember(crew, member);
        if (isAlreadyApplied) {
            throw new CustomException(CrewErrorCode.ALREADY_EXISTS, "이미 해당 크루에 신청 중입니다.");
        }
        crewMemberRepository.save(crewEntityMapper.toCrewMember(crew, member));
    }

    @Override
    @Transactional
    public CrewResponse.CrewScheduleIdResponse registerSchedule(Member member, Long crewId, CrewRequest.createSchedule request) {
        // 크루 존재 여부 확인
        Crew crew = crewRepository.findById(crewId)
                .orElseThrow(() -> new CustomException(CrewErrorCode.NOT_FOUND));

        // 코스 존재 여부 확인
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new CustomException(CourseErrorCode.NOT_FOUND));

        if(!crew.getLeader().getId().equals(member.getId())){
            throw new CustomException(CrewErrorCode.FORBIDDEN_ACCESS);
        }
        boolean isScheduleExist = crewScheduleRepository.existsByCrewAndStartTimeBeforeAndEndTimeAfter(
                crew, request.getEndTime(), request.getStartTime());

        if (isScheduleExist) {
            throw new CustomException(CrewErrorCode.SCHEDULE_CONFLICT);
        }
        CrewSchedule crewSchedule = crewScheduleRepository.save(crewEntityMapper.toCrewSchedule(crew,course,request));
        return new CrewResponse.CrewScheduleIdResponse(crewSchedule.getId());
    }
}
