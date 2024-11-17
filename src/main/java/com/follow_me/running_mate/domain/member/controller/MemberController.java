package com.follow_me.running_mate.domain.member.controller;

import com.follow_me.running_mate.config.security.auth.PrincipalDetails;
import com.follow_me.running_mate.config.security.jwt.JwtTokenProvider;
import com.follow_me.running_mate.domain.member.dto.request.MemberRequest;
import com.follow_me.running_mate.domain.member.dto.response.MemberResponse;
import com.follow_me.running_mate.domain.member.entity.Member;
import com.follow_me.running_mate.domain.member.service.MemberService;
import com.follow_me.running_mate.global.common.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
@Tag(name = "Profile", description = "마이 프로필 조회 API")
public class MemberController {

    private final MemberService memberService;

    @GetMapping
    @Operation(summary = "마이 프로필 조회 API", description = "로그인한 사용자의 프로필 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "프로필 조회에 성공했습니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "AUTH001", description = "인증되지 않은 사용자입니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class)))
    })
    public BaseResponse<MemberResponse.MyProfileResponse> getMyProfile(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        MemberResponse.MyProfileResponse memberProfile = memberService.getMyProfile(principalDetails.getUsername());
        // 이메일을 기반으로 사용자 프로필 조회
        return BaseResponse.success("마이 프로필 조회에 성공했습니다.", memberProfile);
    }
    //마이프로필 수정 api
    @PatchMapping
    @Operation(summary = "마이 프로필 수정 API" , description = "로그인한 사용자의 프로필을 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "프로필 수정에 성공했습니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "AUTH001", description = "인증되지 않은 사용자입니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "MEMBER001", description = "회원을 찾을 수 없습니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "MEMBER002", description = "변경할 프로필 정보가 없습니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class)))
    })
    public BaseResponse<MemberResponse.UpdateMyProfileResponse> updateMyProfile(@AuthenticationPrincipal PrincipalDetails principalDetails ,
        @RequestBody @Valid MemberRequest.UpdateProfileRequest request) {
        MemberResponse.UpdateMyProfileResponse memberProfile = memberService.updateProfile(request , principalDetails.getUsername());
        // 이메일을 기반으로 사용자 프로필 조회
        return BaseResponse.success("마이 프로필 수정에 성공했습니다.", memberProfile);
    }
    //비밀번호 변경 api
    @PatchMapping("/password")
    @Operation(summary = "비밀번호 변경 API", description = "로그인한 사용자의 비밀번호를 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "비밀번호 변경에 성공했습니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "AUTH001", description = "인증되지 않은 사용자입니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "MEMBER001", description = "회원을 찾을 수 없습니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "MEMBER003", description = "현재 비밀번호가 일치하지 않습니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "MEMBER004", description = "새 비밀번호가 현재 비밀번호와 동일합니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class)))
    })
    public BaseResponse<Void> changePassword(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                               @RequestBody @Valid MemberRequest.ChangePasswordRequest request) {
        memberService.changePassword(request, principalDetails.getUsername());
        return BaseResponse.success("비밀번호 변경에 성공했습니다.",null);
    }
    // 배지 조회 API
    @GetMapping("/badges")
    @Operation(summary = "멤버 배지 조회 API", description = "로그인한 사용자의 배지 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "배지 조회에 성공했습니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "AUTH001", description = "인증되지 않은 사용자입니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "MEMBER001", description = "회원을 찾을 수 없습니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class)))
    })
    public BaseResponse<MemberResponse.BadgeListResponse> getMemberBadges(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        List<MemberResponse.BadgeResponse> badges = memberService.getMemberBadges(principalDetails.getUsername());
        MemberResponse.BadgeListResponse badgeListResponse = new MemberResponse.BadgeListResponse(badges);  // List를 BadgeListResponse로 감싸기
        return BaseResponse.success("배지 조회에 성공했습니다.", badgeListResponse);  // 성공 응답으로 감싼 객체 반환
    }
    @GetMapping("/nickname/{nickname}")
    @Operation(summary = "닉네임 중복 확인 API", description = "입력된 닉네임이 중복되었는지 확인합니다. 존재시 true")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "닉네임 중복 확인에 성공했습니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "400", description = "입력 값이 유효하지 않습니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류입니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class)))
    })
    public BaseResponse<Boolean> checkNicknameDuplicate(
            @PathVariable(value = "nickname") String nickname
    ) {
        boolean isDuplicate = memberService.isNicknameDuplicate(nickname); //중복 닉네임 존재시
        return BaseResponse.success("닉네임 중복 확인에 성공했습니다.", isDuplicate);
    }
    @GetMapping("/email/{email}")
    @Operation(summary = "이메일 중복 확인 API", description = "입력된 이메일이 중복되었는지 확인합니다. 존재시 true")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이메일 중복 확인에 성공했습니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "400", description = "입력 값이 유효하지 않습니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류입니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class)))
    })
    public BaseResponse<Boolean> checkEmailDuplicate(
            @PathVariable(value = "email") String email
    ) {
        boolean isDuplicate = memberService.isEmailDuplicate(email);
        return BaseResponse.success("이메일 중복 확인에 성공했습니다.", isDuplicate);
    }
    @GetMapping("/{email}")//TODO: 자신의 프로필 조회 막을건지 고민,에러처리 고도화 필요 , mapper 생성 필요
    @Operation(summary = "상대방 프로필 조회 API", description = "주어진 이메일에 해당하는 사용자의 프로필 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "프로필 조회에 성공했습니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "MEMBER001", description = "회원을 찾을 수 없습니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class)))
    })
    public BaseResponse<MemberResponse.MyProfileResponse> getOtherProfile(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable String email) {

        // 로그인한 사용자의 프로필 정보와 상대방 프로필 정보를 조회
        MemberResponse.MyProfileResponse memberProfile = memberService.getMemberProfileByEmail(email);

        // 상대방 프로필 조회 성공
        return BaseResponse.success("상대방 프로필 조회에 성공했습니다.", memberProfile);
    }


}
