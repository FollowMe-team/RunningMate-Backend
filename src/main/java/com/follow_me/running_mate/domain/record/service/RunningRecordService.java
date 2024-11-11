package com.follow_me.running_mate.domain.record.service;

import com.follow_me.running_mate.domain.member.entity.Member;
import com.follow_me.running_mate.domain.record.entity.RunningRecord;
import java.util.List;

public interface RunningRecordService {

    List<RunningRecord> getRecentCourses(Member member);
}
