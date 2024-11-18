package com.follow_me.running_mate.domain.course.service.record;

import com.follow_me.running_mate.domain.course.dto.request.CourseRequest;
import com.follow_me.running_mate.domain.course.dto.response.CourseResponse;
import com.follow_me.running_mate.domain.course.entity.Course;
import com.follow_me.running_mate.domain.course.entity.CourseRecord;
import com.follow_me.running_mate.domain.course.mapper.CourseEntityMapper;
import com.follow_me.running_mate.domain.course.repository.CourseRecordPointRepository;
import com.follow_me.running_mate.domain.course.repository.CourseRecordRepository;
import com.follow_me.running_mate.domain.member.entity.Member;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CourseRecordServiceImpl implements CourseRecordService {

    private final CourseEntityMapper courseEntityMapper;
    private final CourseRecordRepository courseRecordRepository;
    private final CourseRecordPointRepository courseRecordPointRepository;

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
}
