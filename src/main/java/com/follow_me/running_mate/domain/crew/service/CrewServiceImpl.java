package com.follow_me.running_mate.domain.crew.service;

import com.follow_me.running_mate.domain.course.entity.Course;
import com.follow_me.running_mate.domain.crew.dto.response.CrewResponse;
import com.follow_me.running_mate.domain.crew.entity.Crew;
import com.follow_me.running_mate.domain.crew.entity.CrewMember;
import com.follow_me.running_mate.domain.crew.mapper.CrewResponseMapper;
import com.follow_me.running_mate.domain.crew.repository.CrewCourseRepository;
import java.util.List;

import com.follow_me.running_mate.domain.crew.repository.CrewMemberRepository;
import com.follow_me.running_mate.domain.crew.repository.CrewRepository;
import com.follow_me.running_mate.domain.enums.Status;
import com.follow_me.running_mate.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CrewServiceImpl implements CrewService {

    private final CrewCourseRepository crewCourseRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final CrewResponseMapper crewResponseMapper;
    private final CrewRepository crewRepository;

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
}
