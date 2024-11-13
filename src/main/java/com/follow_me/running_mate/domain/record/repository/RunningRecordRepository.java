package com.follow_me.running_mate.domain.record.repository;

import com.follow_me.running_mate.domain.member.entity.Member;
import com.follow_me.running_mate.domain.record.entity.RunningRecord;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RunningRecordRepository extends JpaRepository<RunningRecord, Long> {

    List<RunningRecord> findTop3ByMemberOrderByStartTimeDesc(Member member);
}
