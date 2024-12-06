package com.follow_me.running_mate.domain.crew.validation.validator;

import com.follow_me.running_mate.domain.crew.repository.CrewRepository;
import com.follow_me.running_mate.domain.crew.validation.annotation.UniqueCrewName;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UniqueCrewNameValidator implements ConstraintValidator<UniqueCrewName, String> {

    private final CrewRepository crewRepository;

    @Override
    public boolean isValid(String name, ConstraintValidatorContext context) {
        return !crewRepository.existsByName(name); // false일 경우 유효성 검사 실패
    }
}
