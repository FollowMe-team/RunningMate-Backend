package com.follow_me.running_mate.domain.course.validation.validator;

import com.follow_me.running_mate.domain.course.repository.CourseRepository;
import com.follow_me.running_mate.domain.course.validation.annotation.UniqueCourseName;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UniqueCourseNameValidator implements ConstraintValidator<UniqueCourseName, String> {

    private final CourseRepository courseRepository;

    @Override
    public boolean isValid(String name, ConstraintValidatorContext context) {
        return courseRepository.existsByName(name);
    }
}
