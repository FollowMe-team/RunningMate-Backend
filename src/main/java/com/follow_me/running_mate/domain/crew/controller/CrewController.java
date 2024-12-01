package com.follow_me.running_mate.domain.crew.controller;

import com.follow_me.running_mate.config.security.auth.PrincipalDetails;
import com.follow_me.running_mate.domain.crew.dto.request.CrewRequest;
import com.follow_me.running_mate.domain.crew.dto.response.CrewResponse;
import com.follow_me.running_mate.domain.crew.service.CrewService;
import com.follow_me.running_mate.domain.enums.ActivityTimeType;
import com.follow_me.running_mate.domain.enums.CrewMemberStatus;
import com.follow_me.running_mate.global.common.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.YearMonth;
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
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable(value = "crewId") Long crewId
    ) {
        return BaseResponse.success(
                "크루 상세 조회에 성공했습니다.",
                crewService.getCrewDetail(principalDetails.member(),crewId)
        );
    }
    @GetMapping("/{crewId}/select")
    @Operation(summary = "크루 선택 조회 API", description = "선택한 크루의 상세 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "크루 선택 조회에 성공했습니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "CREW001", description = "크루를 찾을 수 없습니다.")
    })
    public BaseResponse<CrewResponse.CrewSelectResponse> getCrewSelectList(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable(value = "crewId") Long crewId
    ) {
        return BaseResponse.success(
                "크루 선택 조회에 성공했습니다.",
                crewService.getCrewSelectDetail(principalDetails.member(),crewId)
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
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable(value = "crewId") Long crewId,
            @RequestParam(value = "yearMonth") YearMonth yearMonth) {
        return BaseResponse.success("크루의 월간 스케줄 조회에 성공했습니다.", crewService.getCrewScheduleByDate(principalDetails.member(),crewId, yearMonth));
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
        List<CrewResponse.CrewMemberInfo> responses = crewService.getMembersBySchedule(scheduleId);
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

    @PatchMapping("{crewId}/members/{memberId}")
    @Operation(summary = "크루 신청 상태 업데이트 API", description = "크루 신청 상태를 수락하거나 거절합니다. , 크루 멤버 수정 가능")
    @ApiResponse(responseCode = "200", description = "상태 업데이트 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class)))
    public BaseResponse<Void> updateCrewMemberStatus(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable(value = "crewId") Long crewId,
            @PathVariable(value = "memberId") Long memberId,
            @RequestParam(value = "status") CrewMemberStatus status
    ) {
        crewService.updateCrewMemberStatus(principalDetails.member(), crewId, memberId, status);
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
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable(value = "crewId") Long crewId
    ) {
        return BaseResponse.success("즐겨찾기 코스를 성공적으로 조회했습니다.", crewService.getFavoriteCourses(principalDetails.member(),crewId));
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

    @PatchMapping("/leader/{memberId}")
    @Operation(summary = "러닝 크루 리더 변경 API", description = "주어진 멤버 ID로 크루 리더를 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리더 변경 성공",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "CREW009", description = "해당 크루를 찾을 수 없음",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "CREW004", description = "해당 사용자에게 권한이 없습니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "CREW007", description = "해당 멤버 아이디는 크루에 존재하지 않습니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "MEMBER001", description = "해당 멤버 아이디는 존재하지 않는 회원입니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class)))
    })
    public BaseResponse<Void> changeLeader(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable(value = "memberId") Long memberId
    ) {
        crewService.changeLeader(principalDetails.member(), memberId);
        return BaseResponse.success("리더가 성공적으로 변경되었습니다.", null);
    }

    @DeleteMapping("/{crewId}/cancel")
    @Operation(summary = "러닝 크루 신청 취소 API", description = "주어진 크루 ID에 대해 신청을 취소합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "신청 취소 성공",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "CREW009", description = "해당 크루를 찾을 수 없음",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "CREW004", description = "해당 사용자에게 권한이 없습니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "CREW007", description = "해당 크루에 신청한 적 없는 사용자입니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "CREW013", description = "해당 크루의 크루장입니다. 크루장을 변경해주세요",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class)))
    })
    public BaseResponse<Void> cancelCrewApplication(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable(value = "crewId") Long crewId
    ) {
        crewService.cancelCrewApplication(principalDetails.member(), crewId);
        return BaseResponse.success("신청이 성공적으로 취소되었습니다.", null);
    }

    @DeleteMapping("/{crewId}")
    @Operation(summary = "러닝 크루 삭제 API", description = "주어진 크루 ID에 대해 크루를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "크루 삭제 성공",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "CREW001", description = "해당 크루를 찾을 수 없음",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "CREW004", description = "해당 사용자에게 권한이 없습니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class)))
    })
    public BaseResponse<Void> deleteCrew(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable(value = "crewId") Long crewId
    ) {
        crewService.deleteCrew(principalDetails.member(), crewId);
        return BaseResponse.success("크루가 성공적으로 삭제되었습니다.", null);
    }

    @DeleteMapping("/schedule/{scheduleId}")
    @Operation(summary = "러닝 크루 일정 삭제 API", description = "주어진 크루 일정 Id에 대해 크루 일정을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "크루 일정 삭제 성공",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "CREW009", description = "해당 크루일정을 찾을 수 없음",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "CREW004", description = "해당 사용자에게 권한이 없습니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class)))
    })
    public BaseResponse<Void> deleteCrewSchedule(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable(value = "scheduleId") Long scheduleId
    ) {
        crewService.deleteCrewSchedule(principalDetails.member(), scheduleId);
        return BaseResponse.success("크루 일정이 성공적으로 삭제되었습니다.", null);
    }

    @DeleteMapping("/{crewId}/course/{courseId}/favorite")
    @Operation(summary = "크루 코스 즐겨찾기 삭제 API", description = "주어진 코스 ID에 대해 크루의 즐겨찾기를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "즐겨찾기 삭제 성공",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "CREW014", description = "해당 코스는 즐겨찾기에 존재하지 않습니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "CREW004", description = "해당 사용자에게 권한이 없습니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "CREW001", description = "해당 크루를 찾을 수 없음",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class)))
    })
    public BaseResponse<Void> deleteFavoriteCourse(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable(value = "crewId") Long crewId,
            @PathVariable(value = "courseId") Long courseId
    ) {
        crewService.deleteFavoriteCourse(principalDetails.member(), courseId,crewId);
        return BaseResponse.success("즐겨찾기가 성공적으로 삭제되었습니다.", null);
    }
    @GetMapping("/search")
    @Operation(summary = "크루 검색 API", description = "크루를 검색합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "크루 검색에 성공했습니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
    })
    public BaseResponse<CrewResponse.MyCrewListResponse> searchCrews(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @Parameter(description = "검색어")
            @RequestParam(value = "keyword", required = false) String keyword,

            @Parameter(description = "도시")
            @RequestParam(value = "city", required = false) String city,

            @Parameter(description = "구역")
            @RequestParam(value = "district", required = false) String district,

            @Parameter(description = "활동 시간", example = "MONDAY,SATURDAY",
                    schema = @Schema(implementation = String.class, allowableValues =
                            {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY",
                                    "SUNDAY", "HOLIDAY", "WEEKDAY", "WEEKEND", "EVERYDAY"}))
            @RequestParam(value = "activityTimes", required = false) List<ActivityTimeType> activityTimes
    ) {
        return BaseResponse.success(
                "크루 검색에 성공했습니다.",
                crewService.searchCrews(
                        principalDetails.member(), keyword, city, district, activityTimes
                )
        );
    }
    @GetMapping("/{crewId}/canJoin")
    @Operation(summary = "크루 가입 가능 여부 확인 API", description = "사용자의 랭킹을 기반으로 크루에 가입할 수 있는지 확인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "크루 가입 가능 여부 확인 성공",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class)))
    })
    public BaseResponse<Boolean> canMemberJoinCrew(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable(value = "crewId") Long crewId
    ) {
        return BaseResponse.success("크루 가입 가능 여부 확인 성공", crewService.canMemberJoinCrew(principalDetails.member(), crewId));
    }
}
