package com.follow_me.running_mate.domain.record.service;

import com.follow_me.running_mate.domain.course.entity.Course;
import com.follow_me.running_mate.domain.member.entity.Member;
import com.follow_me.running_mate.domain.record.entity.RunningRecord;
import com.follow_me.running_mate.domain.record.repository.RunningRecordRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RunningRecordServiceImpl implements RunningRecordService {

    private final RunningRecordRepository runningRecordRepository;

    @Override
    public List<Course> getRecentCourses(Member member) {
        return runningRecordRepository.findTop3ByMemberOrderByCreatedAtDesc(member).stream()
            .map(RunningRecord::getCourse)
            .toList();
    }
}
