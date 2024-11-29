package com.follow_me.running_mate.domain.crew.controller;

import com.follow_me.running_mate.config.security.auth.PrincipalDetails;
import com.follow_me.running_mate.domain.crew.dto.request.CrewRequest;
import com.follow_me.running_mate.domain.crew.dto.response.CrewResponse;
import com.follow_me.running_mate.domain.crew.service.CrewService;
import com.follow_me.running_mate.domain.member.dto.response.MemberResponse;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/crew")
@RequiredArgsConstructor
@Tag(name = "Crew", description = "Crew 관리 API")
public class CrewController {

    private final CrewService crewService;

    @GetMapping
    @Operation(summary = "러닝 크루 조회 API", description = "로그인한 사용자가 속한 러닝 크루 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "크루 조회에 성공했습니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "AUTH001", description = "인증되지 않은 사용자입니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class)))
    })
    public BaseResponse<CrewResponse.MyCrewListResponse> getMyCrews(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        CrewResponse.MyCrewListResponse crewListResponse = crewService.getCrewsByMember(principalDetails.member());
        return BaseResponse.success("크루 조회에 성공했습니다.", crewListResponse); // 응답 객체 반환
    }

    @GetMapping("/{crewId}/detail")
    @Operation(summary = "크루 상세 조회 API", description = "크루 상세 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "크루 상세 조회에 성공했습니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
    })
    public BaseResponse<CrewResponse.CrewDetailResponse> getCrewDetail(
            @PathVariable(value = "crewId") Long crewId
    ) {
        return BaseResponse.success(
                "크루 상세 조회에 성공했습니다.",
                crewService.getCrewDetail(crewId)
        );
    }

    @GetMapping("/{crewId}/schedule")
    @Operation(summary = "특정 크루의 월간 스케줄 조회", description = "크루의 ID와 해당 월을 기준으로 스케줄을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "스케줄 조회에 성공했습니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "400", description = "입력 값이 유효하지 않습니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류입니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class)))
    })
    public BaseResponse<CrewResponse.CrewScheduleListResponse> getCrewSchedule(
            @PathVariable(value = "crewId") Long crewId,
            @RequestParam(value = "date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        return BaseResponse.success("크루의 월간 스케줄 조회에 성공했습니다.", crewService.getCrewScheduleByDate(crewId, date));
    }

    @GetMapping("/members/{scheduleId}")
    @Operation(summary = "스케줄 참여 멤버 조회", description = "특정 스케줄에 참여한 멤버들의 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "멤버 조회 성공",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "스케줄을 찾을 수 없음",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class)))
    })
    public BaseResponse<CrewResponse.CrewScheduleMemberListResponse> getMembersBySchedule(@PathVariable(value = "scheduleId") Long scheduleId) {
        List<MemberResponse.FollowResponse> responses = crewService.getMembersBySchedule(scheduleId);
        return BaseResponse.success("스케줄 멤버 조회에 성공했습니다.", new CrewResponse.CrewScheduleMemberListResponse(responses));
    }

    //TODO: 아직 프로필 리스트 화면이 나오지 않아 임시로 팔로워랑 똑같이 작성해둠
    @PostMapping
    @Operation(summary = "크루 생성 API", description = "크루를 생성합니다.")
    @ApiResponse(responseCode = "200", description = "크루 생성에 성공했습니다.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class)))
    public BaseResponse<CrewResponse.CrewIdResponse> createCrew(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestPart(name = "request") @Valid CrewRequest.createCrew request,
            @RequestPart(value = "representativeImage", required = false) MultipartFile representativeImage
    ) {
        return BaseResponse.success("크루 생성에 성공했습니다.", crewService.createCrew(principalDetails.member(), request, representativeImage));
    }

    @PostMapping("/{crewId}/apply")
    @Operation(summary = "크루 신청 API", description = "멤버가 특정 크루에 가입 신청합니다.")
    @ApiResponse(responseCode = "200", description = "신청 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class)))
    public BaseResponse<Void> applyToCrew(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable(value = "crewId") Long crewId
    ) {
        crewService.applyToCrew(principalDetails.member(), crewId);
        return BaseResponse.success("크루 신청이 완료되었습니다.", null);
    }

    @PostMapping("/{crewId}/schedule")
    @Operation(summary = "크루 일정 등록 API", description = "특정 크루에 일정을 등록합니다.")
    @ApiResponse(responseCode = "200", description = "일정 등록 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class)))
    public BaseResponse<CrewResponse.CrewScheduleIdResponse> registerSchedule(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable(value = "crewId") Long crewId,
            @RequestBody @Valid CrewRequest.createSchedule request
    ) {
        return BaseResponse.success("일정이 등록되었습니다.", crewService.registerSchedule(principalDetails.member(), crewId, request));
    }

    @PostMapping("/apply/{scheduleId}")
    @Operation(summary = "크루 일정 참여 신청 API", description = "특정 크루 일정에 참여 신청을 합니다.")
    @ApiResponse(responseCode = "200", description = "참여 신청 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class)))
    public BaseResponse<CrewResponse.CrewScheduleApplyIdResponse> applyToSchedule(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable(value = "scheduleId") Long scheduleId
    ) {
        return BaseResponse.success("일정 참여 신청이 완료되었습니다.", crewService.applyToSchedule(principalDetails.member(), scheduleId));
    }

    @PatchMapping("/{memberId}")
    @Operation(summary = "크루 신청 상태 업데이트 API", description = "크루 신청 상태를 수락하거나 거절합니다.")
    @ApiResponse(responseCode = "200", description = "상태 업데이트 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class)))
    public BaseResponse<Void> updateCrewMemberStatus(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable(value = "memberId") Long memberId,
            @RequestParam(value = "status") String status
    ) {
        crewService.updateCrewMemberStatus(principalDetails.member(), memberId, status);
        return BaseResponse.success("크루 신청 상태가 성공적으로 업데이트되었습니다.", null);
    }

    @PatchMapping("/{crewId}/modify")
    @Operation(summary = "크루 수정 API", description = "크루장이 본인의 크루를 수정합니다.")
    @ApiResponse(responseCode = "200", description = "크루 수정 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class)))
    public BaseResponse<CrewResponse.UpdateCrewResponse> updateCrew(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable(value = "crewId") Long crewId,
            @RequestBody @Valid CrewRequest.UpdateCrewRequest request
    ) {

        return BaseResponse.success("크루 수정에 성공했습니다.", crewService.updateCrew(principalDetails.member(), crewId, request));
        //TODO: 닉네임 중복 확인 마이프로필과 동일하게 해야할지 고민
    }

    @GetMapping("/{crewId}/favorite")
    @Operation(summary = "크루 즐겨찾기 코스 조회 API", description = "특정 크루의 즐겨찾기 코스를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "즐겨찾기 코스 조회 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class)))
    public BaseResponse<CrewResponse.CrewCourseListResponse> getFavoriteCourses(
            @PathVariable(value = "crewId") Long crewId
    ) {
        return BaseResponse.success("즐겨찾기 코스를 성공적으로 조회했습니다.", crewService.getFavoriteCourses(crewId));
    }

    @PostMapping("{crewId}/courses/{courseId}/favorite")
    @Operation(summary = "크루 코스 즐겨찾기 추가 API", description = "특정 크루의 즐겨찾기 코스에 추가합니다.")
    @ApiResponse(responseCode = "200", description = "즐겨찾기 추가 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class)))
    public BaseResponse<CrewResponse.CrewCourseIdResponse> addFavoriteCourse(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable(value = "crewId") Long crewId,
            @PathVariable(value = "courseId") Long courseId
    ) {
        return BaseResponse.success("코스가 즐겨찾기에 추가되었습니다.", crewService.addFavoriteCourse(principalDetails.member(), crewId, courseId));
    }

    @PostMapping("/{crewId}/img")
    @Operation(summary = "크루 활동 사진 업로드 API", description = "크루의 활동 사진을 여러 장 업로드합니다.")
    @ApiResponse(responseCode = "200", description = "이미지 업로드 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class)))
    public BaseResponse<CrewResponse.ActivityImageListResponse> uploadCrewImages(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable(value = "crewId") Long crewId,
            @RequestPart(value = "activityImages") List<MultipartFile> activityImages
    ) {
        return BaseResponse.success("이미지 업로드가 완료되었습니다.", crewService.uploadCrewImages(crewId, activityImages, principalDetails.member()));
    }
    //TODO: 크루 활동 사진 순서에 맞춰서 수정 메소드 짜기

    @PatchMapping("/{crewId}/schedule/{scheduleId}")
    @Operation(summary = "크루 일정 수정 API", description = "크루의 일정을 수정합니다.")
    @ApiResponse(responseCode = "200", description = "일정 수정 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class)))
    public BaseResponse<CrewResponse.UpdateCrewSchedule> updateCrewSchedule(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable(value = "crewId") Long crewId,
            @PathVariable(value = "scheduleId") Long scheduleId,
            @RequestBody @Valid CrewRequest.createSchedule request
    ) {
        return BaseResponse.success("일정이 성공적으로 수정되었습니다.", crewService.updateSchedule(principalDetails.member(), crewId, scheduleId, request));
    }

    @PatchMapping("/schedule/{scheduleId}/cancel")
    @Operation(summary = "크루 일정 참여 취소 API", description = "크루원이 일정 참여를 취소합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "일정 참여 취소 성공",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "CREW009", description = "해당 스케줄을 찾을 수 없음",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "CREW004", description = "해당 사용자에게 권한이 없습니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "CREW011", description = "해당 일정에 신청한 적 없는 사용자입니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class)))
    })
    public BaseResponse<Void> cancelScheduleApply(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable(value = "scheduleId") Long scheduleId
    ) {
        crewService.cancelScheduleApply(principalDetails.member(), scheduleId);
        return BaseResponse.success("일정 참여가 성공적으로 취소되었습니다.", null);
    }

    @PatchMapping("/schedule/{scheduleId}/attend") //TODO: 크루 멤버 아이디로 값을 받을지 고민 갑슬 바로 보기 애매함
    @Operation(summary = "크루 일정 출석 체크 API", description = "크루원이 일정에 출석 여부를 체크합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "출석 체크 성공",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "CREW009", description = "해당 스케줄을 찾을 수 없음",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "CREW004", description = "해당 사용자에게 권한이 없습니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class)))
    })
    public BaseResponse<Void> attendSchedule(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable(value = "scheduleId") Long scheduleId,
            @RequestBody @Valid CrewRequest.attendCrewSchedule memberIds
    ) {
        crewService.attendSchedule(principalDetails.member(), scheduleId, memberIds.getMemberIds());
        return BaseResponse.success("크루 일정 출석체크가 완료되었습니다.", null);
    }
}
