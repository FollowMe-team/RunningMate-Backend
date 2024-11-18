package com.follow_me.running_mate.domain.course.service.point;

import com.follow_me.running_mate.domain.course.dto.request.CourseRequest;
import com.follow_me.running_mate.domain.course.entity.Course;
import com.follow_me.running_mate.domain.course.entity.CoursePoint;
import com.follow_me.running_mate.domain.course.mapper.CourseEntityMapper;
import com.follow_me.running_mate.domain.course.repository.CoursePointRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CoursePointServiceImpl implements CoursePointService {

    private final CourseEntityMapper courseEntityMapper;
    private final CoursePointRepository coursePointRepository;

    @Override
    public void saveCoursePoints(Course course, List<CourseRequest.CoursePointInfo> coursePoints) {
        for (int i = 0; i < coursePoints.size(); i++) {
            CoursePoint coursePoint = courseEntityMapper.toCoursePoint(course, coursePoints.get(i), i + 1);
            coursePointRepository.save(coursePoint);
        }
    }

    @Override
    public List<CoursePoint> getCoursePoints(Course course) {
        return coursePointRepository.findAllByCourseOrderBySequenceNumberAsc(course);
    }
}
