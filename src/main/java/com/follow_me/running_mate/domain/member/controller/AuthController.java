package com.follow_me.running_mate.domain.member.controller;

import com.follow_me.running_mate.config.security.auth.PrincipalDetails;
import com.follow_me.running_mate.domain.member.dto.request.MemberRequest;
import com.follow_me.running_mate.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody MemberRequest.SignUpRequest request) {
        // TODO: 프로필 이미지도 추가
        return ResponseEntity.ok(memberService.signup(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        memberService.logout(principalDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/withdraw")
    public ResponseEntity<Void> withdraw(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        memberService.withdraw(principalDetails.member());
        return ResponseEntity.ok().build();
    }
}
