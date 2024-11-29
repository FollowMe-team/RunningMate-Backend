package com.follow_me.running_mate.domain.member.controller;

import com.follow_me.running_mate.config.security.auth.PrincipalDetails;
import com.follow_me.running_mate.domain.course.dto.response.CourseResponse;
import com.follow_me.running_mate.domain.member.dto.request.MemberRequest;
import com.follow_me.running_mate.domain.member.dto.response.MemberResponse;
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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
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
        MemberResponse.MyProfileResponse memberProfile = memberService.getMyProfile(principalDetails.getUsername());
        // 이메일을 기반으로 사용자 프로필 조회
        return BaseResponse.success("마이 프로필 조회에 성공했습니다.", memberProfile);
    }
    //마이프로필 수정 api
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
    public BaseResponse<Void> changePassword(
        @AuthenticationPrincipal PrincipalDetails principalDetails,
        @RequestBody @Valid MemberRequest.ChangePasswordRequest request
    ) {
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
    public BaseResponse<MemberResponse.BadgeListResponse> getMemberBadges(
        @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        return BaseResponse.success("배지 조회에 성공했습니다.", memberService.getMemberBadges(principalDetails.member()));
    }
    @GetMapping("/check/nickname=")
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
            @RequestParam(value = "nickname") String nickname
    ) {
        boolean isDuplicate = memberService.isNicknameDuplicate(nickname); // 중복 닉네임 존재 시
        return BaseResponse.success("닉네임 중복 확인에 성공했습니다.", isDuplicate);
    }

    @GetMapping("/check/email=")
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
            @RequestParam(value = "email") String email
    ) {
        boolean isDuplicate = memberService.isEmailDuplicate(email);
        return BaseResponse.success("이메일 중복 확인에 성공했습니다.", isDuplicate);
    }

    @GetMapping("/{email}")//TODO: 자신의 프로필 조회 막을건지 고민,에러처리 고도화 필요 , mapper 생성 필요 , 인증 절차 필요할까?
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

    @GetMapping("/record")
    @Operation(summary = "마이 기록 조회 (캘린더)", description = "자신이 선택한 날짜의 코스 기록을 확인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "코스 기록 조회에 성공했습니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "400", description = "입력 값이 유효하지 않습니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류입니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class)))
    })
    public BaseResponse<CourseResponse.CourseRecordInfoList> getMyRunningRecord(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestParam(value = "date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {

        List<CourseResponse.CourseRecordInfo> response = memberService.getMemberRunningRecords(principalDetails.member(), date);
        return BaseResponse.success("해당 일자의 코스 조회에 성공했습니다.", new CourseResponse.CourseRecordInfoList(response));
    }
    @GetMapping("/follow") // 내 팔로우 목록 조회 API
    @Operation(summary = "사용자 팔로우 조회 API", description = "로그인한 사용자가 팔로우한 사용자 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "팔로우 목록 조회에 성공했습니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "MEMBER001", description = "회원을 찾을 수 없습니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class)))
    })
    public BaseResponse<MemberResponse.FollowListResponse> getFollowList(
            @AuthenticationPrincipal PrincipalDetails principalDetails) {

        // 로그인한 사용자의 팔로우 목록 조회
        List<MemberResponse.FollowResponse> followList = memberService.getFollowList(principalDetails.member());
        // 팔로우 목록 조회 성공
        return BaseResponse.success("팔로우 목록 조회에 성공했습니다.", new MemberResponse.FollowListResponse(followList));
    }
    @GetMapping("/follower") // 사용자 팔로워 목록 조회 API
    @Operation(summary = "사용자 팔로워 조회 API", description = "로그인한 사용자를 팔로우한 사용자 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "팔로워 목록 조회에 성공했습니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "MEMBER001", description = "회원을 찾을 수 없습니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class)))
    })
    public BaseResponse<MemberResponse.FollowerListResponse> getFollowerList(
            @AuthenticationPrincipal PrincipalDetails principalDetails) {

        List<MemberResponse.FollowResponse> followerList = memberService.getFollowerList(principalDetails.member());

        // 팔로워 목록 조회 성공
        return BaseResponse.success("팔로워 목록 조회에 성공했습니다.", new MemberResponse.FollowerListResponse(followerList));
    }
    //TODO:팔로우 화면 나오면 response 수정하기

    @PostMapping("/follow/{id}")
    @Operation(summary = "팔로우 추가 API", description = "특정 사용자를 팔로우하거나, 기존에 팔로우했던 사용자를 활성화합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "팔로우 처리에 성공했습니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "해당 사용자를 찾을 수 없습니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "400", description = "자기 자신을 팔로우할 수 없습니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
    })
    public BaseResponse<Void> follow(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable(value = "id") Long id) {

        memberService.follow(principalDetails.member(), id);
        return BaseResponse.success("팔로우 처리에 성공했습니다.", null);
    }

    @PatchMapping("/follow/{id}")
    @Operation(summary = "팔로우 취소 API", description = "특정 사용자의 팔로우 상태를 비활성화합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "팔로우 취소에 성공했습니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "팔로우 대상이 존재하지 않습니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "400", description = "자기 자신과의 팔로우 상태를 변경할 수 없습니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
    })
    public BaseResponse<Void> unfollow(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable(value = "id") Long id) {

        memberService.unfollow(principalDetails.member(), id);
        return BaseResponse.success("팔로우 취소에 성공했습니다.", null);
    }

}
