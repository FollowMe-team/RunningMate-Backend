package com.follow_me.running_mate.domain.crew.validation.annotation;

import com.follow_me.running_mate.domain.crew.validation.validator.UniqueCrewNameValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueCrewNameValidator.class)
public @interface UniqueCrewName {
    String message() default "이미 사용 중인 크루 이름입니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
