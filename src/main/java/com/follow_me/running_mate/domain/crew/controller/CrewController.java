package com.follow_me.running_mate.domain.crew.controller;

import com.follow_me.running_mate.config.security.auth.PrincipalDetails;
import com.follow_me.running_mate.domain.crew.dto.response.CrewResponse;
import com.follow_me.running_mate.domain.crew.service.CrewService;
import com.follow_me.running_mate.global.common.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
                crewService.getCrewDetail(principalDetails.member(), crewId)
        );
    }

}
