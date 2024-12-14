package com.follow_me.running_mate.domain.member.service.auth;

import com.follow_me.running_mate.domain.member.dto.request.MemberRequest;
import com.follow_me.running_mate.domain.member.entity.Member;
import org.springframework.web.multipart.MultipartFile;

public interface AuthService{

    void signup(MemberRequest.SignUpRequest request, MultipartFile profileImage);
    void logout(String email);
    void withdraw(Member member, MemberRequest.WithdrawRequest request);

}
