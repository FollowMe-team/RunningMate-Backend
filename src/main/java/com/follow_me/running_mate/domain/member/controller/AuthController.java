package com.follow_me.running_mate.domain.member.controller;

import static com.follow_me.running_mate.config.security.jwt.JwtConstant.REFRESH_TOKEN_HEADER;

import com.follow_me.running_mate.config.security.auth.PrincipalDetails;
import com.follow_me.running_mate.domain.member.dto.request.MemberRequest;
import com.follow_me.running_mate.domain.member.service.MemberService;
import com.follow_me.running_mate.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "회원 인증 API")
public class AuthController {

    private final MemberService memberService;

    @PostMapping("/login")
    @Operation(summary = "로그인 API", description = "이메일과 비밀번호로 로그인합니다.")
    public void login(@RequestBody MemberRequest.LoginRequest request) {
        // 실제 구현은 필터에서 처리되므로 빈 메서드
    }

    @PostMapping("/refresh")
    @Operation(summary = "토큰 재발급 API", description = "리프레시 토큰을 이용해 엑세스 토큰을 재발급합니다.")
    public void refresh(
        @Parameter(description = "리프레시 토큰", required = true)
        @RequestHeader(REFRESH_TOKEN_HEADER) String refreshToken
    ) {
        // 실제 구현은 필터에서 처리되므로 빈 메서드
    }

    @PostMapping("/signup")
    @Operation(summary = "회원 가입 API")

    public ApiResponse<Void> signup(@RequestBody @Valid MemberRequest.SignUpRequest request) {
        // TODO: 프로필 이미지도 추가
        memberService.signup(request);
        return ApiResponse.success("회원 가입에 성공했습니다.", null);
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃 API")
    public ApiResponse<Void> logout(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        memberService.logout(principalDetails.getUsername());
        return ApiResponse.success("로그아웃에 성공했습니다.", null);
    }

    @DeleteMapping("/withdraw")
    @Operation(summary = "회원 탈퇴 API")
    public ApiResponse<Void> withdraw(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        memberService.withdraw(principalDetails.member());
        return ApiResponse.success("회원 탈퇴에 성공했습니다.", null);
    }
}
