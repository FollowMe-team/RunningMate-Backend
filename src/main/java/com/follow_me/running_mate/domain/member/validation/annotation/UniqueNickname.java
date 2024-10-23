package com.follow_me.running_mate.domain.member.validation.annotation;

import com.follow_me.running_mate.domain.member.validation.validator.UniqueNicknameValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueNicknameValidator.class)
public @interface UniqueNickname {
    String message() default "이미 사용 중인 닉네임입니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
