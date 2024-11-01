package com.follow_me.running_mate.domain.test.controller;

import com.follow_me.running_mate.global.common.BaseResponse;
import com.follow_me.running_mate.global.common.service.S3ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
@Tag(name = "Image", description = "이미지 관리 API")
public class S3ImageController {

    private final S3ImageService s3ImageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "이미지 업로드 API", description = "S3에 이미지를 업로드합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이미지가 업로드되었습니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "COMMON012", description = "파일을 찾을 수 없어 업로드 실패",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "COMMON013", description = "이미지 업로드 중 오류가 발생했습니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "COMMON002", description = "이미지 파일 확장자가 존재하지 않습니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "COMMON014", description = "지원하지 않는 파일 확장자 형식",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),


    })
    public BaseResponse<String> uploadImage(@RequestPart MultipartFile image) {
        // 이미지 업로드 처리
        String imageAddress = s3ImageService.upload(image);
        return BaseResponse.success("이미지가 업로드되었습니다.", imageAddress);
    }

    @DeleteMapping
    @Operation(summary = "이미지 삭제 API", description = "S3에서 이미지를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이미지가 삭제되었습니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "COMMON012", description = "해당 파일을 찾을 수 없습니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "COMMON012", description = "URL 데이터 오류 입니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
    })
    public BaseResponse<Void> deleteImage(@RequestParam String imageAddress) {
        s3ImageService.deleteImageFromS3(imageAddress);
        return BaseResponse.success("이미지가 삭제되었습니다.", null);
    }
}
