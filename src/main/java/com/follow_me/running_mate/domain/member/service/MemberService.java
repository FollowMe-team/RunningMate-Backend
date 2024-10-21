package com.follow_me.running_mate.domain.member.service;

import com.follow_me.running_mate.domain.member.dto.request.MemberRequest;
import com.follow_me.running_mate.domain.member.entity.Member;

public interface MemberService {

    String signup(MemberRequest.SignUpRequest request);
    void logout(String email);
    void withdraw(Member member);
}
