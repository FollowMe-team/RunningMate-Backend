package com.follow_me.running_mate.domain.course.controller;

import com.follow_me.running_mate.config.security.auth.PrincipalDetails;
import com.follow_me.running_mate.domain.course.dto.response.CourseResponse;
import com.follow_me.running_mate.domain.course.service.CourseService;
import com.follow_me.running_mate.domain.enums.CourseOptionType;
import com.follow_me.running_mate.domain.enums.Difficulty;
import com.follow_me.running_mate.domain.enums.ReviewSortType;
import com.follow_me.running_mate.domain.enums.RunningGoal;
import com.follow_me.running_mate.global.common.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
@Tag(name = "Course", description = "코스 API")
public class CourseController {

    private final CourseService courseService;

    @GetMapping("/recent")
    @Operation(summary = "최근 코스 조회 API", description = "최근 생성된 코스를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "최근 코스 조회에 성공했습니다."),
    })
    public BaseResponse<CourseResponse.CourseListResponse> getRecentCourses(
        @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        return BaseResponse.success(
            "최근 코스 조회에 성공했습니다.", courseService.getRecentCourses(principalDetails.member())
        );
    }

    @GetMapping("/bookmark")
    @Operation(summary = "즐겨찾기한 코스 조회 API", description = "즐겨찾기한 코스를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "즐겨찾기한 코스 조회에 성공했습니다."),
    })
    public BaseResponse<CourseResponse.CourseListResponse> getBookmarkedCourses(
        @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        return BaseResponse.success(
            "즐겨찾기한 코스 조회에 성공했습니다.", courseService.getBookmarkedCourses(principalDetails.member())
        );
    }

    @GetMapping("/my")
    @Operation(summary = "내 코스 조회 API", description = "내가 생성한 코스를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "내가 생성한 코스 조회에 성공했습니다."),
    })
    public BaseResponse<CourseResponse.MyCourseListResponse> getMyCourses(
        @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        return BaseResponse.success(
            "내가 생성한 코스 조회에 성공했습니다.", courseService.getMyCourses(principalDetails.member())
        );
    }

    @GetMapping
    @Operation(summary = "코스 추천 API", description = "코스를 추천합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "코스 추천에 성공했습니다."),
    })
    public BaseResponse<CourseResponse.CourseListResponse> recommendedCourses(
        @AuthenticationPrincipal PrincipalDetails principalDetails,
        @Parameter(description = "위도") @RequestParam(value = "latitude", required = false) Double latitude,
        @Parameter(description = "경도") @RequestParam(value = "longitude", required = false) Double longitude,
        @Parameter(description = "난이도") @RequestParam(value = "difficulty", required = false) Difficulty difficulty,
        @Parameter(description = "러닝 목표") @RequestParam(value = "runningGoal", required = false) RunningGoal runningGoal
    ) {
        return BaseResponse.success(
            "코스 추천에 성공했습니다.", courseService.recommendedCourses(
                principalDetails.member(), latitude, longitude, difficulty, runningGoal
            )
        );
    }

    @GetMapping("/search")
    @Operation(summary = "코스 검색 API", description = "코스를 검색합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "코스 검색에 성공했습니다."),
    })
    public BaseResponse<CourseResponse.CourseListResponse> searchCourses(
        @AuthenticationPrincipal PrincipalDetails principalDetails,
        @Parameter(description = "검색어")
        @RequestParam(value = "keyword") String keyword,

        @Parameter(description = "위도")
        @RequestParam(value = "latitude", required = false) Double latitude,

        @Parameter(description = "경도")
        @RequestParam(value = "longitude", required = false) Double longitude,

        @Parameter(description = "난이도", example = "EASY,NORMAL",
            schema = @Schema(implementation = String.class, allowableValues = {"EASY", "NORMAL", "HARD"}))
        @RequestParam(value = "difficulties", required = false) List<Difficulty> difficulties,

        @Parameter(description = "코스 옵션", example = "FOREST,RIVERSIDE",
            schema = @Schema(implementation = String.class, allowableValues = {
                "FOREST", "RIVERSIDE", "LAKESIDE", "MOUNTAIN", "SEASIDE",
                "CITYSCAPE", "PARK", "TRAIL", "CAMPUS", "TRACK",
                "GRADIENT_HIGH", "GRADIENT_MIDDLE", "GRADIENT_LOW",
                "GRADIENT_NONE", "DOG_WALKABLE", "BICYCLE_WALKABLE", "BABY_WALKABLE"
            }))
        @RequestParam(value = "options", required = false) List<CourseOptionType> options
    ) {
        return BaseResponse.success(
            "코스 검색에 성공했습니다.",
            courseService.searchCourses(
                principalDetails.member(), keyword, latitude, longitude, difficulties, options
            )
        );
    }

    @GetMapping("/{courseId}/detail")
    @Operation(summary = "코스 상세 조회 API", description = "코스 상세 정보를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "코스 상세 조회에 성공했습니다."),
    })
    public BaseResponse<CourseResponse.CourseDetailResponse> getCourseDetail(
        @AuthenticationPrincipal PrincipalDetails principalDetails,
        @PathVariable(value = "courseId") Long courseId
    ) {
        return BaseResponse.success(
            "코스 상세 조회에 성공했습니다.", courseService.getCourseDetail(principalDetails.member(), courseId)
        );
    }

    @GetMapping("/{courseId}/reviews")
    @Operation(summary = "코스 리뷰 조회 API", description = "코스 리뷰를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "코스 리뷰 조회에 성공했습니다."),
    })
    public BaseResponse<CourseResponse.CourseReviewListResponse> getCourseReviews(
        @AuthenticationPrincipal PrincipalDetails principalDetails,
        @PathVariable(value = "courseId") Long courseId,
        @Parameter(description = "정렬 기준") @RequestParam(required = false, defaultValue = "LATEST") ReviewSortType sortType
    ) {
        return BaseResponse.success(
            "코스 리뷰 조회에 성공했습니다.",
            courseService.getCourseReviews(principalDetails.member(), courseId, sortType)
        );
    }

    @GetMapping("/{courseId}/path")
    @Operation(summary = "코스 경로 조회 API", description = "코스 경로를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "코스 경로 조회에 성공했습니다."),
    })
    public BaseResponse<CourseResponse.CoursePathResponse> getCoursePath(
        @PathVariable(value = "courseId") Long courseId
    ) {
        return BaseResponse.success(
            "코스 경로 조회에 성공했습니다.", courseService.getCoursePath(courseId)
        );
    }
}
