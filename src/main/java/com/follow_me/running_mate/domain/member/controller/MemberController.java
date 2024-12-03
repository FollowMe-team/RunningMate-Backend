package com.follow_me.running_mate.domain.member.controller;

import com.follow_me.running_mate.config.security.auth.PrincipalDetails;
import com.follow_me.running_mate.domain.course.dto.response.CourseResponse;
import com.follow_me.running_mate.domain.member.dto.request.MemberRequest;
import com.follow_me.running_mate.domain.member.dto.response.MemberResponse;
import com.follow_me.running_mate.domain.member.service.MemberService;
import com.follow_me.running_mate.global.common.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.YearMonth;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
@Tag(name = "Member", description = "마이 프로필 조회 API")
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
    public BaseResponse<MemberResponse.MyProfileResponse> getMyProfile(
        @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        return BaseResponse.success(
            "마이 프로필 조회에 성공했습니다.", memberService.getMyProfile(principalDetails.member())
        );
    }

    @GetMapping("/summary")
    @Operation(summary = "마이 프로필 요약 조회 API", description = "마이 프로필 변경 시, 필요한 요약 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "프로필 요약 조회에 성공했습니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "AUTH001", description = "인증되지 않은 사용자입니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class)))
    })
    public BaseResponse<MemberResponse.MyProfileSummaryResponse> getMyProfileSummary(
        @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        return BaseResponse.success(
            "마이 프로필 요약 조회에 성공했습니다.", memberService.getMyProfileSummary(principalDetails.member())
        );
    }

    @PatchMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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
    public BaseResponse<MemberResponse.UpdateMyProfileResponse> updateMyProfile(
        @AuthenticationPrincipal PrincipalDetails principalDetails ,
        @RequestPart(name = "request") @Valid MemberRequest.UpdateProfileRequest request,
        @RequestPart(name = "profileImage", required = false) MultipartFile profileImage
    ) {
        return BaseResponse.success(
            "마이 프로필 수정에 성공했습니다.",
            memberService.updateProfile(principalDetails.member(), request, profileImage)
        );
    }

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
    public BaseResponse<Void> changePassword(
        @AuthenticationPrincipal PrincipalDetails principalDetails,
        @RequestBody @Valid MemberRequest.ChangePasswordRequest request
    ) {
        memberService.changePassword(request, principalDetails.member());
        return BaseResponse.success("비밀번호 변경에 성공했습니다.",null);
    }

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
    public BaseResponse<MemberResponse.BadgeListResponse> getMemberBadges(
        @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        return BaseResponse.success("배지 조회에 성공했습니다.", memberService.getMemberBadges(principalDetails.member()));
    }

    @GetMapping("/check/nickname=")
    @Operation(summary = "닉네임 중복 확인 API", description = "입력된 닉네임이 중복되었는지 확인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "닉네임 중복 확인에 성공했습니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "400", description = "입력 값이 유효하지 않습니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류입니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class)))
    })
    public BaseResponse<MemberResponse.DuplicateCheckResponse> checkNicknameDuplicate(
            @RequestParam(value = "nickname") String nickname
    ) {
        return BaseResponse.success("닉네임 중복 확인에 성공했습니다.", memberService.isNicknameDuplicate(nickname));
    }

    @GetMapping("/check/email=")
    @Operation(summary = "이메일 중복 확인 API", description = "입력된 이메일이 중복되었는지 확인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이메일 중복 확인에 성공했습니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "400", description = "입력 값이 유효하지 않습니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류입니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class)))
    })
    public BaseResponse<MemberResponse.DuplicateCheckResponse> checkEmailDuplicate(
            @RequestParam(value = "email") String email
    ) {
        return BaseResponse.success("이메일 중복 확인에 성공했습니다.", memberService.isEmailDuplicate(email));
    }

    @GetMapping("/{memberId}")
    @Operation(summary = "타인 프로필 조회 API", description = "해당 사용자이ㅡ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "프로필 조회에 성공했습니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "MEMBER001", description = "회원을 찾을 수 없습니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "MEMBER006", description = "본인 프로필은 마이 프로필 조회 API를 통해 확인해주세요.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class)))
    })
    public BaseResponse<MemberResponse.OtherProfileResponse> getOtherProfile(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable Long memberId) {

        return BaseResponse.success(
            "타인 프로필 조회에 성공했습니다.",
            memberService.getOtherProfile(principalDetails.member(), memberId)
        );
    }

    @GetMapping("/records/monthly")
    @Operation(summary = "나의 러닝 기록 조회(월별)", description = "나의 월별 러닝 기록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "나의 월별 러닝 기록 조회에 성공했습니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class)))
    })
    public BaseResponse<CourseResponse.CourseRecordInfoList> getMyCourseRecords(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestParam(value = "yearMonth") YearMonth yearMonth
    ) {
        return BaseResponse.success(
            "나의 월별 러닝 기록 조회에 성공했습니다.",
            memberService.getMyCourseRecords(principalDetails.member(), yearMonth));
    }

    @GetMapping("/{memberId}/records/monthly")
    @Operation(summary = "타인 러닝 기록 조회(월별)", description = "타인의 월별 러닝 기록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "타인의 월별 러닝 기록 조회에 성공했습니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class)))
    })
    public BaseResponse<CourseResponse.CourseRecordInfoList> getOtherCourseRecords(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable Long memberId,
            @RequestParam(value = "yearMonth") YearMonth yearMonth
    ) {
        return BaseResponse.success(
            "타인의 월별 러닝 기록 조회에 성공했습니다.",
            memberService.getOtherCourseRecords(principalDetails.member(), memberId, yearMonth));
    }

    @GetMapping("/follow")
    @Operation(summary = "사용자 팔로잉 조회 API", description = "로그인한 사용자가 팔로잉한 사용자 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "팔로잉 목록 조회에 성공했습니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class)))
    })
    public BaseResponse<MemberResponse.FollowingListResponse> getFollowList(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        return BaseResponse.success(
            "팔로잉 목록 조회에 성공했습니다.",
            memberService.getFollowingList(principalDetails.member())
        );
    }

    @GetMapping("/follower")
    @Operation(summary = "사용자 팔로워 조회 API", description = "로그인한 사용자를 팔로우한 사용자 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "팔로워 목록 조회에 성공했습니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class)))
    })
    public BaseResponse<MemberResponse.FollowerListResponse> getFollowerList(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        return BaseResponse.success(
            "팔로워 목록 조회에 성공했습니다.",
            memberService.getFollowerList(principalDetails.member())
        );
    }

    @PostMapping("/follow/{memberId}")
    @Operation(summary = "팔로우 추가 API", description = "특정 사용자를 팔로우하거나, 기존에 팔로우했던 사용자를 활성화합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "팔로우 처리에 성공했습니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "MEMBER001", description = "회원을 찾을 수 없습니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "MEMBER008", description = "존재하지 않는 팔로잉 사용자입니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "MEMBER009", description = "본인을 대상으로 할 수 없습니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "MEMBER010", description = "이미 팔로우 중인 사용자입니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class)))
    })
    public BaseResponse<Void> follow(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable(value = "memberId") Long memberId
    ) {
        memberService.follow(principalDetails.member(), memberId);
        return BaseResponse.success("팔로우 처리에 성공했습니다.", null);
    }

    @DeleteMapping("/follow/{memberId}")
    @Operation(summary = "팔로우 취소 API", description = "특정 사용자의 팔로우 상태를 비활성화합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "팔로우 취소에 성공했습니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "MEMBER001", description = "회원을 찾을 수 없습니다.",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "MEMBER008", description = "존재하지 않는 대상 사용자입니다.",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "MEMBER009", description = "본인을 대상으로 할 수 없습니다.",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "MEMBER011", description = "팔로우 중이 아닌 사용자입니다.",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class)))
    })
    public BaseResponse<Void> unfollow(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable(value = "memberId") Long memberId
    ) {
        memberService.unfollow(principalDetails.member(), memberId);
        return BaseResponse.success("팔로우 취소에 성공했습니다.", null);
    }



    @GetMapping(value = {"/footprint/{memberId}", "/footprint" })
    @Operation(summary = "발자국 조회 API", description = "특정 사용자에게 남긴 발자국을 조회합니다.(memberId가 없을 시 자신의 발자국 조회)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "발자국 조회에 성공했습니다.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
        @ApiResponse(responseCode = "MEMBER001", description = "회원을 찾을 수 없습니다.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
        @ApiResponse(responseCode = "MEMBER008", description = "존재하지 않는 대상 사용자입니다.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
    })
    public BaseResponse<MemberResponse.FootprintListResponse> getFootprints(
        @AuthenticationPrincipal PrincipalDetails principalDetails,
        @PathVariable(value = "memberId", required = false) Long memberId
    ) {
        return BaseResponse.success(
            "발자국 조회에 성공했습니다.",
            memberService.getFootprints(principalDetails.member(), memberId)
        );
    }

    @PostMapping("/footprint/{memberId}")
    @Operation(summary = "발자국 남기기 API", description = "특정 사용자에게 발자국을 남깁니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "발자국 남기기에 성공했습니다.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
        @ApiResponse(responseCode = "MEMBER001", description = "회원을 찾을 수 없습니다.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
        @ApiResponse(responseCode = "MEMBER008", description = "존재하지 않는 대상 사용자입니다.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
        @ApiResponse(responseCode = "MEMBER009", description = "본인을 대상으로 할 수 없습니다.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
    })
    public BaseResponse<Void> createFootprint(
        @AuthenticationPrincipal PrincipalDetails principalDetails,
        @PathVariable(value = "memberId") Long memberId,
        @RequestBody @Valid MemberRequest.FootprintRequest request
    ) {
        memberService.createFootprint(principalDetails.member(), memberId, request);
        return BaseResponse.success("발자국 남기기에 성공했습니다.", null);
    }
}
