package com.follow_me.running_mate.domain.crew.service;

import com.follow_me.running_mate.domain.course.entity.Course;
import com.follow_me.running_mate.domain.crew.entity.Crew;
import com.follow_me.running_mate.domain.crew.repository.CrewCourseRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CrewServiceImpl implements CrewService {

    private final CrewCourseRepository crewCourseRepository;

    @Override
    public List<Crew> getCrewByCourse(Course course) {
        return crewCourseRepository.findDistinctCrewByCourse(course);
    }
}
