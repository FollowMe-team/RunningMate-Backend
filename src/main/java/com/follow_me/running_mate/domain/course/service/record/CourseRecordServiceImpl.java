package com.follow_me.running_mate.domain.course.service.record;

import com.follow_me.running_mate.domain.course.dto.request.CourseRequest;
import com.follow_me.running_mate.domain.course.dto.response.CourseResponse;
import com.follow_me.running_mate.domain.course.entity.Course;
import com.follow_me.running_mate.domain.course.entity.CourseRecord;
import com.follow_me.running_mate.domain.course.mapper.CourseEntityMapper;
import com.follow_me.running_mate.domain.course.mapper.CourseResponseMapper;
import com.follow_me.running_mate.domain.course.repository.CourseRecordPointRepository;
import com.follow_me.running_mate.domain.course.repository.CourseRecordRepository;
import com.follow_me.running_mate.domain.member.entity.Member;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CourseRecordServiceImpl implements CourseRecordService {

    private final CourseEntityMapper courseEntityMapper;
    private final CourseRecordRepository courseRecordRepository;
    private final CourseRecordPointRepository courseRecordPointRepository;
    private final CourseResponseMapper courseResponseMapper;

    @Override
    public List<Course> getRecentCourses(Member member) {
        return courseRecordRepository.findTop3ByRunnerOrderByStartTimeDesc(member).stream()
            .map(CourseRecord::getCourse)
            .toList();
    }

    @Override
    public CourseResponse.CourseRecordIdResponse createCourseRecord(
        Member member, Course course, CourseRequest.CreateCourseRecordRequest request
    ) {
        CourseRecord courseRecord = courseRecordRepository.save(
            courseEntityMapper.toCourseRecord(course, member, request)
        );

        request.getRecordPoints().stream()
            .map(point -> courseEntityMapper.toCourseRecordPoint(courseRecord, point))
            .map(courseRecordPointRepository::save)
            .forEach(courseRecord::addRecordPoint);

        return new CourseResponse.CourseRecordIdResponse(courseRecord.getId());
    }
    @Override
    @Transactional(readOnly = true)
    public CourseResponse.CourseRecordInfoList getRecordsByMonth(
        Member member, YearMonth yearMonth, Boolean isMine
    ) {

        // 날짜의 시작과 끝을 설정
        LocalDateTime startOfMonth = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = yearMonth.atEndOfMonth().atTime(LocalTime.MAX);

        // 해당 날짜와 회원의 코스 기록을 조회
        List<CourseRecord> courseRecords = courseRecordRepository.findAllByRunnerAndStartTimeBetween(
            member, startOfMonth, endOfMonth
        );

        return new CourseResponse.CourseRecordInfoList(
            courseRecords.stream().map(courseRecord ->
                courseResponseMapper.toCourseRecordInfo(courseRecord, isMine)
            ).toList()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseRecord> getRecordsByMember(Member member) {
        return courseRecordRepository.findAllByRunner(member);
    }
}
