package com.follow_me.running_mate.domain.crew.service;

import com.follow_me.running_mate.domain.course.entity.Course;
import com.follow_me.running_mate.domain.crew.entity.Crew;
import java.util.List;

public interface CrewService {
    List<Crew> getCrewByCourse(Course course);
}
