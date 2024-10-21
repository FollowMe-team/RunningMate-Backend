package com.follow_me.running_mate.domain.member.mapper;

import com.follow_me.running_mate.domain.enums.Role;
import com.follow_me.running_mate.domain.member.dto.request.MemberRequest;
import com.follow_me.running_mate.domain.member.entity.Member;
import org.springframework.stereotype.Component;

@Component
public class MemberMapper {

    public Member toEntity(MemberRequest.SignUpRequest request, String encodedPassword) {
        return Member.builder()
            .email(request.getEmail())
            .password(encodedPassword)
            .name(request.getName())
            .gender(request.getGender())
            .birth(request.getBirth())
            .runningGoal(request.getRunningGoal())
            .nickname(request.getNickname())
            .runningCareer(request.getRunningCareer())
            .role(Role.USER)
            .build();
    }
}
