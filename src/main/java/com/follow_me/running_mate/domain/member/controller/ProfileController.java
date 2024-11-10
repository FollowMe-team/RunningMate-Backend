package com.follow_me.running_mate.domain.member.controller;

import com.follow_me.running_mate.config.security.jwt.JwtTokenProvider;
import com.follow_me.running_mate.domain.member.dto.response.MemberResponse;
import com.follow_me.running_mate.domain.member.service.MemberService;
import com.follow_me.running_mate.global.common.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "Profile", description = "마이 프로필 조회 API")
public class ProfileController {

    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/members")
    @Operation(summary = "마이 프로필 조회 API", description = "로그인한 사용자의 프로필 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "프로필 조회에 성공했습니다."),
            @ApiResponse(responseCode = "AUTH001", description = "인증되지 않은 사용자입니다."),
    })
    public BaseResponse<MemberResponse.MyProfileResponse> getMyProfile(
            @RequestHeader("Authorization") String authorizationHeader // 헤더에서 Access Token 을 받음
    ) {
        // Authorization: Bearer <AccessToken> 형태에서 토큰만 추출
        String accessToken = authorizationHeader.replace("Bearer ", "");

        // 토큰에서 이메일 추출
        String email = jwtTokenProvider.getEmailFromToken(accessToken);

        // 이메일을 기반으로 사용자 프로필 조회
        MemberResponse.MyProfileResponse profile = memberService.getMyProfile(email);
        return BaseResponse.success("마이 프로필 조회에 성공했습니다.", profile);
    }
}
