package com.follow_me.running_mate.domain.member.validation.validator;

import com.follow_me.running_mate.domain.member.repository.MemberRepository;
import com.follow_me.running_mate.domain.member.validation.annotation.UniqueNickname;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UniqueNicknameValidator implements ConstraintValidator<UniqueNickname, String> {

    private final MemberRepository memberRepository;

    @Override
    public boolean isValid(String nickname, ConstraintValidatorContext context) {
        return memberRepository.findByNickname(nickname).isEmpty();
    }
}