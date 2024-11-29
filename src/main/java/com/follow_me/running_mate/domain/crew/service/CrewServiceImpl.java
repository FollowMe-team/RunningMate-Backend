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
import java.util.concurrent.atomic.AtomicInteger;

import com.follow_me.running_mate.domain.enums.CrewScheduleApplyStatus;
import com.follow_me.running_mate.domain.enums.Status;
import com.follow_me.running_mate.domain.member.dto.response.MemberResponse;
import com.follow_me.running_mate.domain.member.entity.Member;
import com.follow_me.running_mate.domain.member.exception.MemberErrorCode;
import com.follow_me.running_mate.domain.member.mapper.MemberMapper;
import com.follow_me.running_mate.domain.member.repository.MemberRepository;
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
    private final MemberRepository memberRepository;
    private final CrewImageRepository crewImageRepository;

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
    public CourseResponse.CourseListResponse getCrewCourses(Crew crew) {

        List<Course> myCourses = crewCourseRepository.findTop3CoursesByCrewOrderByCreatedAtDesc(crew);

        List<CourseResponse.SummaryInfo> courses = myCourses.stream().map(course ->
                courseResponseMapper.toCrewCourseInfo(
                        course,
                        courseReviewService.getAverageRating(course),
                        course.getRunningCount(),
                        courseOptionService.getCourseOptions(course),
                        coursePointService.getCoursePoints(course)
                )).toList();

        return new CourseResponse.CourseListResponse(courses);
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
    public CourseResponse.CourseListResponse getCrewScheduleCourses(Course course) {

        CourseResponse.SummaryInfo courseInfo = courseResponseMapper.toCrewCourseInfo(
                course,
                courseReviewService.getAverageRating(course),
                course.getRunningCount(),
                courseOptionService.getCourseOptions(course),
                coursePointService.getCoursePoints(course));
        return new CourseResponse.CourseListResponse(List.of(courseInfo));
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

        if (!crew.getLeader().getId().equals(member.getId())) {
            throw new CustomException(CrewErrorCode.FORBIDDEN_ACCESS);
        }
        boolean isScheduleExist = crewScheduleRepository.existsByCrewAndStartTimeBeforeAndEndTimeAfter(
                crew, request.getEndTime(), request.getStartTime());

        if (isScheduleExist) {
            throw new CustomException(CrewErrorCode.SCHEDULE_CONFLICT);
        }
        CrewSchedule crewSchedule = crewScheduleRepository.save(crewEntityMapper.toCrewSchedule(crew, course, request));
        return new CrewResponse.CrewScheduleIdResponse(crewSchedule.getId());
    }

    @Override
    @Transactional
    public CrewResponse.CrewScheduleApplyIdResponse applyToSchedule(Member member, Long scheduleId) {
        // 일정 존재 여부 확인
        CrewSchedule crewSchedule = crewScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new CustomException(CrewErrorCode.NOT_FOUND));

        // 크루에 이미 가입된 멤버인지 확인
        CrewMember crewMember = crewMemberRepository.findByCrewAndMember(crewSchedule.getCrew(), member)
                .orElseThrow(() -> new CustomException(CrewErrorCode.FORBIDDEN_ACCESS));

        if (!crewMember.getStatus().equals(Status.COMPLETE)) {
            throw new CustomException(CrewErrorCode.FORBIDDEN_ACCESS);
        }

        // 일정 최대 인원 초과 여부 확인
        if (crewSchedule.getMemberCount() >= crewSchedule.getMemberMax()) {
            throw new CustomException(CrewErrorCode.SCHEDULE_FULL);
        }

        // 중복 신청 확인
        boolean isAlreadyApplied = crewScheduleApplyRepository.existsByCrewScheduleAndCrewMember(crewSchedule, crewMember);
        if (isAlreadyApplied) {
            throw new CustomException(CrewErrorCode.ALREADY_EXISTS);
        }
        CrewScheduleApply crewScheduleApply = crewScheduleApplyRepository.save(crewEntityMapper.toCrewScheduleApply(crewSchedule, crewMember));
        crewSchedule.increaseMemberCount();
        return new CrewResponse.CrewScheduleApplyIdResponse(crewScheduleApply.getId());
    }

    @Override
    @Transactional
    public void updateCrewMemberStatus(Member currentUser, Long memberId, String status) {

        Member applyMember = memberRepository.findById(memberId).orElseThrow(() -> new CustomException(MemberErrorCode.NOT_FOUND));
        Crew crew = crewRepository.findByLeader(currentUser).orElseThrow(() -> new CustomException(CrewErrorCode.NOT_FOUND));
        CrewMember crewMember = crewMemberRepository.findByCrewAndMember(crew, applyMember)
                .orElseThrow(() -> new CustomException(CrewErrorCode.NOAPPLY_CREW));

        if (crewMember.getStatus().equals(Status.COMPLETE) || crewMember.getStatus().equals(Status.REJECT)) {
            throw new CustomException(CrewErrorCode.ALREADY_EXISTS);
        }

        // 현재 사용자가 해당 크루의 리더인지 확인하는 로직
        validateCrewLeader(currentUser, crewMember);

        try {
            Status newStatus = Status.valueOf(status.toUpperCase());
            crewMember.updateStatus(newStatus);
            if (newStatus.equals(Status.COMPLETE)) {
                crewMember.getCrew().increaseMemberCount();
            }
        } catch (IllegalArgumentException e) {
            throw new CustomException(CrewErrorCode.INVALID_INPUT_VALUE);
        }
    }

    private void validateCrewLeader(Member currentUser, CrewMember crewMember) {
        if (!crewMember.getCrew().getLeader().getId().equals(currentUser.getId())) {
            throw new CustomException(CrewErrorCode.FORBIDDEN_ACCESS);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public CrewResponse.CrewCourseListResponse getFavoriteCourses(Long crewId) {
        Crew crew = crewRepository.getCrew(crewId);
        // CrewCourse 목록 조회
        List<CrewCourse> crewCourses = crewCourseRepository.findByCrew(crew);
        List<CourseResponse.SummaryInfo> courses = crewCourses.stream().map(course ->
                courseResponseMapper.toCrewCourseInfo(
                        course.getCourse(),
                        courseReviewService.getAverageRating(course.getCourse()),
                        course.getCourse().getRunningCount(),
                        courseOptionService.getCourseOptions(course.getCourse()),
                        coursePointService.getCoursePoints(course.getCourse())
                )).toList();
        return new CrewResponse.CrewCourseListResponse(crewId, courses);

    }

    @Override
    @Transactional
    public CrewResponse.CrewCourseIdResponse addFavoriteCourse(Member member, Long crewId, Long courseId) {
        // 크루 존재 확인
        Crew crew = crewRepository.getCrew(crewId);
        if (!member.getId().equals(crew.getLeader().getId())) {
            throw new CustomException(CrewErrorCode.FORBIDDEN_ACCESS);
        }
        // 코스 존재 확인
        Course course = courseRepository.getCourse(courseId);

        // 이미 즐겨찾기된 코스인지 확인
        if (crewCourseRepository.existsByCrewIdAndCourseId(crewId, courseId)) {
            throw new CustomException(CrewErrorCode.DUPLICATE_RESOURCE);
        }

        // 즐겨찾기 추가
        CrewCourse crewCourse = CrewCourse.builder()
                .crew(crew)
                .course(course)
                .build();
        return new CrewResponse.CrewCourseIdResponse(crewCourseRepository.save(crewCourse).getId());
    }
    @Override
    @Transactional
    public CrewResponse.ActivityImageListResponse uploadCrewImages(Long crewId, List<MultipartFile> images,Member member) {
        // 크루 존재 확인
        Crew crew = crewRepository.getCrew(crewId);
        if (!crew.getLeader().getId().equals(member.getId())) {
            throw new CustomException(CrewErrorCode.FORBIDDEN_ACCESS);
        }
        int currentImageCount = crewImageRepository.countByCrew(crew);

        List<CrewImage> crewImages = saveImages(crew,images,currentImageCount+1);

        return new CrewResponse.ActivityImageListResponse(crewResponseMapper.toCrewActivityImages(crewImages));
    }

    @Override
    @Transactional
    public List<CrewImage> saveImages(
            Crew crew, List<MultipartFile> images, Integer orderNumber
            ) {
        AtomicInteger index = new AtomicInteger(orderNumber);
        return images.stream()
                .map(s3ImageService::upload)
                .map(url -> CrewImage.builder()
                        .crew(crew)
                        .url(url)
                        .orderNumber(index.getAndIncrement())
                        .build())
                .map(crewImageRepository::save)
                .toList();
    }

    @Override
    @Transactional
    public CrewResponse.UpdateCrewResponse updateCrew(Member member, Long crewId, CrewRequest.UpdateCrewRequest request) {
        // 크루 조회
        Crew crew = crewRepository.findById(crewId)
                .orElseThrow(() -> new CustomException(CrewErrorCode.NOT_FOUND, "크루를 찾을 수 없습니다."));

        if (!crew.getLeader().getId().equals(member.getId())) {
            throw new CustomException(CrewErrorCode.FORBIDDEN_ACCESS, "크루를 수정할 권한이 없습니다.");
        }

        // 크루 정보 수정
        crew.update(request.getName(), request.getShortDescription(), request.getDetailDescription(), request.getOpenChatUrl());
        // 기존 ActivityTime 삭제
        crewActivityTimeRepository.deleteByCrew(crew);


        List<CrewActivityTime> crewActivityTimeList = crewActivityTimeRepository.saveAll(crewEntityMapper.toCrewActivityTimes(crew, request.getActivityTimes()));
        return crewResponseMapper.toUpdateCrewInfo(crew, crewActivityTimeList);

//        // 기존 ActivityTime 조회
//        List<CrewActivityTime> existingActivityTimes = crewActivityTimeRepository.findByCrew(crew);
//
//        // 요청 데이터 분석
//        Map<Long, CrewRequest.UpdateCrewRequest.ActivityTime> requestMap = request.getActivityTimes().stream()
//                .filter(activityTime -> activityTime.getId() != null) // ID가 있는 경우 기존 데이터로 간주
//                .collect(Collectors.toMap(CrewRequest.UpdateCrewRequest.ActivityTime::getId, Function.identity()));
//
//        List<CrewActivityTime> toDelete = new ArrayList<>();
//        List<CrewActivityTime> toUpdate = new ArrayList<>();
//        List<CrewActivityTime> toAdd = new ArrayList<>();
//
//        // 기존 데이터와 요청 데이터 비교
//        for (CrewActivityTime existing : existingActivityTimes) {
//            CrewRequest.UpdateCrewRequest.ActivityTime requestActivity = requestMap.get(existing.getId());
//            if (requestActivity == null) {
//                // 요청에 없으면 삭제
//                toDelete.add(existing);
//            } else {
//                // 존재하지만 값이 다르면 수정
//                if (!existing.getStartTime().equals(requestActivity.getStartTime())
//                        || !existing.getEndTime().equals(requestActivity.getEndTime())
//                        || !existing.getType().equals(requestActivity.getType())) {
//                    existing.update(requestActivity.getStartTime(), requestActivity.getEndTime(), requestActivity.getType());
//                    toUpdate.add(existing);
//                }
//                requestMap.remove(existing.getId()); // 처리된 요청 데이터는 제거
//            }
//        }
//
//        // 요청 데이터 중 추가된 항목 처리
//        for (CrewRequest.UpdateCrewRequest.ActivityTime remaining : requestMap.values()) {
//            toAdd.add(CrewActivityTime.builder()
//                    .crew(crew)
//                    .startTime(remaining.getStartTime())
//                    .endTime(remaining.getEndTime())
//                    .type(remaining.getType())
//                    .build());
//        }
//
//        // 데이터베이스에 변경 사항 반영
//        crewActivityTimeRepository.deleteAll(toDelete);
//        crewActivityTimeRepository.saveAll(toUpdate);
//        crewActivityTimeRepository.saveAll(toAdd);
    }

}
