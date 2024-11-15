package com.follow_me.running_mate.domain.course.service;

import com.follow_me.running_mate.domain.course.entity.Course;
import com.follow_me.running_mate.domain.course.entity.CourseOption;
import com.follow_me.running_mate.domain.course.mapper.CourseEntityMapper;
import com.follow_me.running_mate.domain.course.repository.CourseOptionRepository;
import com.follow_me.running_mate.domain.enums.CourseOptionType;
import com.follow_me.running_mate.domain.enums.RunningGoal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CourseOptionServiceImpl implements CourseOptionService {

    private final CourseEntityMapper courseEntityMapper;

    private final CourseOptionRepository courseOptionRepository;


    @Override
    public List<CourseOption> saveAll(Course course, List<CourseOptionType> options) {
         return options.stream()
             .map(option -> courseEntityMapper.toCourseOption(course, option))
             .map(courseOptionRepository::save)
             .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseOption> getCourseOptions(Course course) {
        return courseOptionRepository.findAllByCourse(course);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getOptionByRunningGoal(RunningGoal runningGoal) {
        return getOptionsByRunningGoal(runningGoal).stream()
            .map(CourseOptionType::name)
            .toList();
    }

    private List<CourseOptionType> getOptionsByRunningGoal(RunningGoal runningGoal) {
        return switch (runningGoal) {
            case WEIGHT_LOSS ->
                List.of(CourseOptionType.GRADIENT_MIDDLE, CourseOptionType.PARK, CourseOptionType.TRAIL);
            case ENDURANCE ->
                List.of(CourseOptionType.MOUNTAIN, CourseOptionType.FOREST, CourseOptionType.GRADIENT_HIGH);
            case SPEED -> List.of(CourseOptionType.TRACK, CourseOptionType.GRADIENT_NONE, CourseOptionType.CITYSCAPE);
            default -> List.of(); // 목표가 없으면 모든 코스 허용
        };
    }
}
