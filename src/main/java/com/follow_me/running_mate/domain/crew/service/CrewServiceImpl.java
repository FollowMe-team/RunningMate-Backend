package com.follow_me.running_mate.domain.crew.service;

import com.follow_me.running_mate.domain.course.dto.response.CourseResponse;
import com.follow_me.running_mate.domain.course.entity.Course;
import com.follow_me.running_mate.domain.course.mapper.CourseResponseMapper;
import com.follow_me.running_mate.domain.course.repository.CourseRepository;
import com.follow_me.running_mate.domain.course.service.bookmark.CourseBookmarkService;
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

import com.follow_me.running_mate.domain.enums.CrewMemberStatus;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import com.follow_me.running_mate.domain.enums.ActivityTimeType;
import com.follow_me.running_mate.domain.enums.CrewScheduleApplyStatus;
import com.follow_me.running_mate.domain.enums.Status;
import com.follow_me.running_mate.domain.member.entity.Member;
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
    private final CrewEntityMapper crewEntityMapper;
    private final S3ImageService s3ImageService;
    private final CourseRepository courseRepository;
    private final MemberRepository memberRepository;
    private final CrewImageRepository crewImageRepository;
    private final CourseBookmarkService courseBookmarkService;

    @Override
    public List<Crew> getCrewByCourse(Course course) {
        return crewCourseRepository.findDistinctCrewByCourse(course);
    }

    @Override
    @Transactional
    public CrewResponse.MyCrewListResponse getCrewsByMember(Member member) {
        List<Crew> myCrews = crewMemberRepository.findCrewsByMemberAndStatus(member, CrewMemberStatus.COMPLETE);
        List<Long> sumFootprint = crewMemberRepository.sumFootprintByCrewsAndStatus(myCrews,CrewMemberStatus.COMPLETE);
        List<Crew> recommendedCrews = crewRepository.findTop4ByIdNotInOrderByCreatedAtDesc(myCrews);
        List<Long> recommendSumFootprint = crewMemberRepository.sumFootprintByCrewsAndStatus(recommendedCrews,CrewMemberStatus.COMPLETE);
        //TODO: 분리하자
        return new CrewResponse.MyCrewListResponse(crewResponseMapper.toCrewInfoResponse(myCrews,sumFootprint),
                crewResponseMapper.toCrewInfoResponse(recommendedCrews,recommendSumFootprint));
    }

    @Override
    @Transactional
    public CrewResponse.CrewDetailResponse getCrewDetail(Member member ,Long crewId) {
        Crew crew = crewRepository.getCrew(crewId);

        return crewResponseMapper.toCrewDetailInfo(
                crew,
                getCrewActivityTime(crew),
                getCrewLocationInfo(crew),
                this.getCrewCourses(member,crew)
        );
    }

    private List<CrewActivityTime> getCrewActivityTime(Crew crew) {
        return crewActivityTimeRepository.findAllByCrew(crew);
    }

    private CrewLocation getCrewLocationInfo(Crew crew) {
        return crewLocationRepository.findByCrew(crew).orElseThrow
                (() -> new CustomException(CrewErrorCode.NOT_FOUND_CREW_LOCATION));
        //TODO:그리고 이 활동시간의 경우
        //같은 시간대인 요일은 묶고, 그리고 월,화,수 순으로 정렬되도록 보내줘야할 거 같은데
        //디자인처럼
    }
    private CourseResponse.CourseListResponse getCrewCourses(Member member , Crew crew) {

        List<Course> myCourses = crewCourseRepository.findTop3CoursesByCrewOrderByCreatedAtDesc(crew);

        List<CourseResponse.SummaryInfo> courses = myCourses.stream().map(course ->
                courseResponseMapper.toSummaryInfo(
                        course,
                        courseReviewService.getAverageRating(course),
                        course.getRunningCount(),
                        courseBookmarkService.isBookmarked(member, course),
                        courseOptionService.getCourseOptions(course),
                        coursePointService.getCoursePoints(course)
                )).toList();

        return new CourseResponse.CourseListResponse(courses);
    }

    @Override
    @Transactional(readOnly = true)
    public CrewResponse.CrewScheduleListResponse getCrewScheduleByDate(Member member, Long crewId, YearMonth yearMonth) {

        LocalDateTime startOfMonth = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = yearMonth.atEndOfMonth().atTime(LocalTime.MAX);
        Crew crew = crewRepository.getCrew(crewId);
        List<CrewSchedule> crewSchedules = crewScheduleRepository.findByCrewAndStartTimeBetween(crew, startOfMonth, endOfMonth);

        List<CrewResponse.CrewScheduleInfo> scheduleInfos = crewSchedules.stream().map(schedule ->
                crewResponseMapper.toCrewScheduleInfo(
                        schedule,
                        getCrewScheduleCourses(member,schedule.getCourse())
                )).toList();

        return CrewResponse.CrewScheduleListResponse.builder()
                .crewId(crew.getId())
                .IsCrewLeader(isUserLeaderOfCrew(member, crew))
                .crewSchedule(scheduleInfos)
                .build();
    }
    private CourseResponse.SummaryInfo getCrewScheduleCourses(Member member,Course course) {
        return courseResponseMapper.toSummaryInfo(
                course,
                courseReviewService.getAverageRating(course),
                course.getRunningCount(),
                courseBookmarkService.isBookmarked(member, course),
                courseOptionService.getCourseOptions(course),
                coursePointService.getCoursePoints(course));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CrewResponse.CrewMemberInfo> getMembersBySchedule(Long scheduleId) {
        CrewSchedule crewSchedule = crewScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new CustomException(CrewErrorCode.NOT_FOUND_SCHEDULE));
        List<CrewMember> crewMembers = crewScheduleApplyRepository
                .findAllCrewMembersByScheduleIdAndStatus(crewSchedule, CrewScheduleApplyStatus.PARTICIPATE);
        return crewMembers.stream()
                .map(crewMember -> crewResponseMapper.toCrewMemberInfo(crewMember.getMember()))
                .toList();
    }

    @Override
    @Transactional
    public CrewResponse.CrewIdResponse createCrew(Member leader, CrewRequest.createCrew request
            , MultipartFile representativeImage) {
        Crew crew;
        String imageUrl = (representativeImage != null) ? s3ImageService.upload(representativeImage) : null;
        crew = crewEntityMapper.toCrew(leader, request, imageUrl);
        Crew savedCrew = crewRepository.save(crew);
        crewLocationRepository.save(crewEntityMapper.toCrewLocation(savedCrew,request));
        crewMemberRepository.save(CrewMember.builder()
                .crew(savedCrew)
                .status(CrewMemberStatus.COMPLETE)
                .member(leader)
                .build());

        // CrewActivityTime 생성 및 저장
        List<CrewActivityTime> activityTimes = crewEntityMapper.toCrewActivityTimes(savedCrew, request.getActivityTimes());
        crewActivityTimeRepository.saveAll(activityTimes);

        return new CrewResponse.CrewIdResponse(crew.getId());
    }

    @Override
    @Transactional
    public void applyToCrew(Member member, Long crewId) {
        // 크루 존재 여부 확인
        Crew crew = crewRepository.getCrew(crewId);
        // 이미 신청했는지 확인
        Optional<CrewMember> crewMember = crewMemberRepository.findByCrewAndMember(crew, member);
        if (crewMember.isEmpty()) {
            crewMemberRepository.save(crewEntityMapper.toCrewMember(crew, member));
        }
        else if(crewMember.get().getStatus() == CrewMemberStatus.REJECT) {
           crewMember.get().updateStatus(CrewMemberStatus.READY);
        }
        throw new CustomException(CrewErrorCode.CREW_APPLY_REJECT);
    }

    @Override
    @Transactional
    public CrewResponse.CrewScheduleIdResponse registerSchedule(Member member, Long crewId, CrewRequest.CreateSchedule request) {
        // 크루 존재 여부 확인
        Crew crew = crewRepository.getCrew(crewId);

        // 코스 존재 여부 확인
        Course course = courseRepository.getCourse(request.getCourseId());

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

        if (!crewMember.getStatus().equals(CrewMemberStatus.COMPLETE)) {
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
    public void updateCrewMemberStatus(Member currentUser, Long crewId, Long memberId, CrewMemberStatus status) {

        Member applyMember = memberRepository.getMember(memberId);
        Crew crew = crewRepository.getCrew(crewId);
        CrewMember crewMember = crewMemberRepository.findByCrewAndMember(crew, applyMember)
                .orElseThrow(() -> new CustomException(CrewErrorCode.NO_APPLY_CREW));

        // 현재 사용자가 해당 크루의 리더인지 확인하는 로직
        validateCrewLeader(currentUser, crewMember);

        switch (status) {
            case COMPLETE:
                crew.increaseMemberCount();
                // TODO: 크루 가입 승인 푸시 알림
                break;
            case REJECT:
                // TODO: 크루 가입 거절 푸시 알림
                break;
            case OUT:
                crew.decreaseMemberCount();
                // TODO: 크루 추방 푸시 알림
                break;
            default:
                throw new CustomException(CrewErrorCode.INVALID_INPUT_VALUE);
        }
        crewMember.updateStatus(status);
    }

    private void validateCrewLeader(Member currentUser, CrewMember crewMember) {
        if (!crewMember.getCrew().getLeader().getId().equals(currentUser.getId())) {
            throw new CustomException(CrewErrorCode.FORBIDDEN_ACCESS);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public CrewResponse.CrewCourseListResponse getFavoriteCourses(Member member, Long crewId) {
        Crew crew = crewRepository.getCrew(crewId);
        // CrewCourse 목록 조회
        List<CrewCourse> crewCourses = crewCourseRepository.findAllByCrew(crew);
        List<CourseResponse.SummaryInfo> courses = crewCourses.stream().map(course ->
                courseResponseMapper.toSummaryInfo(
                        course.getCourse(),
                        courseReviewService.getAverageRating(course.getCourse()),
                        course.getCourse().getRunningCount(),
                        courseBookmarkService.isBookmarked(member, course.getCourse()),
                        courseOptionService.getCourseOptions(course.getCourse()),
                        coursePointService.getCoursePoints(course.getCourse())
                )).toList();

        return new CrewResponse.CrewCourseListResponse(crewId, isUserLeaderOfCrew(member, crew), courses);
    }

    @Override
    @Transactional
    public CrewResponse.CrewCourseIdResponse addFavoriteCourse(Member member, Long crewId, Long courseId) {
        // 크루 존재 확인
        Crew crew = crewRepository.getCrew(crewId);
        validateCrewLeader(member, crew);
        // 코스 존재 확인
        Course course = courseRepository.getCourse(courseId);

        // 이미 즐겨찾기된 코스인지 확인
        if (crewCourseRepository.existsByCrewAndCourse(crew, course)) {
            throw new CustomException(CrewErrorCode.DUPLICATE_RESOURCE);
        }

        return new CrewResponse.CrewCourseIdResponse(
            crewCourseRepository.save(crewEntityMapper.toCrewCourse(crew, course)).getId()
        );
    }

    @Override
    @Transactional
    public void uploadCrewImages(
        Member member, Long crewId, List<MultipartFile> images
    ) {
        // 크루 존재 확인
        Crew crew = crewRepository.getCrew(crewId);
        validateCrewLeader(member, crew);

        int currentImageCount = crewImageRepository.countByCrew(crew);

        saveImages(crew, images, currentImageCount + 1);
    }

    @Override
    @Transactional
    public CrewResponse.CrewScheduleIdResponse updateSchedule(
        Member member, Long scheduleId, CrewRequest.CreateSchedule request
    ) {
        CrewSchedule schedule = crewScheduleRepository.getCrewSchedule(scheduleId);

        validateCrewLeader(member, schedule.getCrew());

        Course course = courseRepository.getCourse(request.getCourseId());
        schedule.update(course, request);

        return new CrewResponse.CrewScheduleIdResponse(schedule.getId());
    }

    @Override
    @Transactional
    public void cancelScheduleApply(Member member, Long scheduleId) {
        CrewSchedule crewSchedule = crewScheduleRepository.getCrewSchedule(scheduleId);

        // 크루에 이미 가입된 멤버인지 확인
        CrewMember crewMember = crewMemberRepository.findByCrewAndMember(crewSchedule.getCrew(), member)
                .orElseThrow(() -> new CustomException(CrewErrorCode.FORBIDDEN_ACCESS));

        // 참여 신청 존재 여부 확인
        CrewScheduleApply crewScheduleApply =
            crewScheduleApplyRepository.findByCrewScheduleAndCrewMember(crewSchedule, crewMember)
                .orElseThrow(() -> new CustomException(CrewErrorCode.APPLY_NOT_FOUND));

        crewScheduleApply.setStatus(CrewScheduleApplyStatus.CANCEL);
        crewSchedule.decreaseMemberCount();
        crewScheduleApply.delete();
    }

    @Override
    @Transactional
    public void attendSchedule(Member member, Long scheduleId, List<Long> memberIds) {
        CrewSchedule crewSchedule = crewScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new CustomException(CrewErrorCode.NOT_FOUND_SCHEDULE));
        if (member.getId().equals(crewSchedule.getCrew().getLeader().getId())) {
            throw new CustomException(CrewErrorCode.FORBIDDEN_ACCESS);
        }
        List<CrewScheduleApply> applyList = crewScheduleApplyRepository.findAllByCrewScheduleId(scheduleId);

        for (CrewScheduleApply apply : applyList) {
            Long existingMemberId = apply.getCrewMember().getMember().getId();
            if (memberIds.contains(existingMemberId)) {
                apply.setStatus(CrewScheduleApplyStatus.PARTICIPATE);
            } else {
                apply.setStatus(CrewScheduleApplyStatus.ABSENCE);
            }
        }
    }

    @Override
    @Transactional
    public void changeLeader(Member currentMember, Long newLeaderId) {
        Crew crew = crewRepository.findByLeader(currentMember)
                .orElseThrow(() -> new CustomException(CrewErrorCode.FORBIDDEN_ACCESS));

        CrewMember crewMember = crewMemberRepository.findByMemberIdAndCrew(newLeaderId, crew)
                .orElseThrow(() -> new CustomException(CrewErrorCode.NO_APPLY_CREW));
        if (!crewMember.getStatus().equals(CrewMemberStatus.COMPLETE)) {
            throw new CustomException(CrewErrorCode.NO_APPLY_CREW);
        }
        Member newLeader = memberRepository.getMember(newLeaderId);

        crew.setLeader(newLeader);
    }

    @Override
    @Transactional
    public void cancelCrewApplication(Member member, Long crewId) {
        Crew crew = crewRepository.getCrew(crewId);

        CrewMember crewMember = crewMemberRepository.findByCrewAndMember(crew, member)
                .orElseThrow(() -> new CustomException(CrewErrorCode.NO_APPLY_CREW));

        if (crew.getLeader().getId().equals(member.getId())) {
            throw new CustomException(CrewErrorCode.CREW_LEADER);
        }
        crewMember.delete();
    }

    @Override
    @Transactional
    public void deleteCrew(Member member, Long crewId) {
        Crew crew = crewRepository.getCrew(crewId);

        if (!crew.getLeader().getId().equals(member.getId())) {
            throw new CustomException(CrewErrorCode.FORBIDDEN_ACCESS);
        }

        crewActivityTimeRepository.findAllByCrew(crew).forEach(CrewActivityTime::delete);
        crewCourseRepository.findAllByCrew(crew).forEach(CrewCourse::delete);
        crewImageRepository.findAllByCrew(crew).forEach(CrewImage::delete);
        crewLocationRepository.findAllByCrew(crew).forEach(CrewLocation::delete);
        crewMemberRepository.findAllByCrew(crew).forEach(CrewMember::delete);

        List<CrewSchedule> schedules = crewScheduleRepository.findAllByCrew(crew);
        for (CrewSchedule schedule : schedules) {
            crewScheduleApplyRepository.findAllByCrewSchedule(schedule).forEach(CrewScheduleApply::delete);
            schedule.delete();
        }
        crew.delete();
        crewRepository.save(crew);
    }

    @Override
    @Transactional
    public void deleteCrewSchedule(Member member, Long scheduleId) {
        CrewSchedule crewSchedule = crewScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new CustomException(CrewErrorCode.NOT_FOUND_SCHEDULE));

        if (!isUserLeaderOfCrew(member, crewSchedule.getCrew())) {
            throw new CustomException(CrewErrorCode.FORBIDDEN_ACCESS);
        }

        crewScheduleApplyRepository.findAllByCrewSchedule(crewSchedule).forEach(CrewScheduleApply::delete);

        crewSchedule.delete();
    }

    @Override
    @Transactional
    public CrewResponse.CrewSelectResponse getCrewSelectDetail(Member member, Long crewId) {
        Crew crew = crewRepository.getCrew(crewId);
        List<CrewImage> crewImages = crewImageRepository.findAllByCrewOrderByOrderNumberAsc(crew);

        return crewResponseMapper.toCrewSelectResponse(crew,crewImages,isUserLeaderOfCrew(member,crew));
    }

    private boolean isUserLeaderOfCrew(Member member, Crew crew) {
        return crew.getLeader().getId().equals(member.getId());
    }

    @Override
    @Transactional
    public void deleteFavoriteCourse(Member member, Long courseId, Long crewId) {
        Crew crew = crewRepository.getCrew(crewId);
        if (!isUserLeaderOfCrew(member, crew)) {
            throw new CustomException(CrewErrorCode.FORBIDDEN_ACCESS);
        }

        CrewCourse crewCourse = crewCourseRepository.findByCrewAndCourseId(crew, courseId)
                .orElseThrow(() -> new CustomException(CrewErrorCode.NOT_FOUND_CREW_COURSE));
        crewCourse.delete();
    }

    @Override
    @Transactional(readOnly = true)
    public CrewResponse.MyCrewListResponse searchCrews(
            Member member, String keyword, String city,
            String district, List<ActivityTimeType> activityTimes
    ) {
        List<Crew> myCrewMembers = crewMemberRepository.findCrewsByMemberAndStatus(member, CrewMemberStatus.COMPLETE);
        List<String> activityTimeList = (activityTimes != null) ? activityTimes.stream()
                .map(ActivityTimeType::name)
                .toList() : List.of();

        List<Crew> searchCrews = crewRepository.searchCrews(
                keyword, city, district, activityTimeList
        );
        List<Long> sumFootprint = crewMemberRepository.sumFootprintByCrewsAndStatus(myCrewMembers,CrewMemberStatus.COMPLETE);
        List<Long> searchSumFootprint = crewMemberRepository.sumFootprintByCrewsAndStatus(searchCrews,CrewMemberStatus.COMPLETE);
        return new CrewResponse.MyCrewListResponse(crewResponseMapper.toCrewInfoResponse(myCrewMembers,sumFootprint),
                crewResponseMapper.toCrewInfoResponse(searchCrews,searchSumFootprint));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canMemberJoinCrew(Member currentUser, Long crewId) {

        Crew crew = crewRepository.getCrew(crewId);
        // 3. 크루의 가입 조건을 확인

        return crew.canJoin(currentUser);
    }

    @Override
    @Transactional
    public CrewResponse.CrewIdResponse updateCrew(
        Member member, Long crewId, CrewRequest.UpdateCrewRequest request, MultipartFile representativeImage
    ) {
        // 크루 조회
        Crew crew = crewRepository.getCrew(crewId);
        validateCrewLeader(member, crew);

        // 크루 정보 수정
        crew.update(request);
        // 크루 활동 위치 수정
        CrewLocation crewLocation = crewLocationRepository.findByCrew(crew)
                .orElseThrow(() -> new CustomException(CrewErrorCode.NOT_FOUND_CREW_LOCATION));
        crewLocation.updateCrewLocation(request.getCity(), request.getDistrict());
        // 기존 ActivityTime 삭제
        crewActivityTimeRepository.deleteByCrew(crew);

        crewActivityTimeRepository.saveAll(
            crewEntityMapper.toCrewActivityTimes(crew, request.getActivityTimes())
        );

        return new CrewResponse.CrewIdResponse(crew.getId());
    }

    private void validateCrewLeader(Member member, Crew crew) {
        if (!crew.getLeader().getId().equals(member.getId())) {
            throw new CustomException(CrewErrorCode.FORBIDDEN_ACCESS);
        }
    }

    private void saveImages(
        Crew crew, List<MultipartFile> images, Integer orderNumber
    ) {
        AtomicInteger index = new AtomicInteger(orderNumber);
        images.stream()
            .map(s3ImageService::upload)
            .map(url -> CrewImage.builder()
                .crew(crew)
                .url(url)
                .orderNumber(index.getAndIncrement())
                .build())
            .map(crewImageRepository::save)
            .toList();
    }
}
