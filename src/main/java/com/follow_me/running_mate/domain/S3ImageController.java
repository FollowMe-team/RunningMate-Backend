package com.follow_me.running_mate.domain;

import com.follow_me.running_mate.global.common.BaseResponse;
import com.follow_me.running_mate.global.common.S3ImageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/images")
public class S3ImageController {

    private final S3ImageService s3ImageService;

    public S3ImageController(S3ImageService s3ImageService) {
        this.s3ImageService = s3ImageService;
    }

    @PostMapping
    public ResponseEntity<BaseResponse<String>> uploadImage(@RequestPart MultipartFile image) {
        // 이미지 업로드
        String imageUrl = s3ImageService.upload(image);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.success("이미지 업로드 성공", imageUrl));
    }

    @DeleteMapping("/delete/{imageAddress}")
    public ResponseEntity<BaseResponse<String>> deleteImage(@PathVariable String imageAddress) {
        // 이미지 삭제
        s3ImageService.deleteImageFromS3(imageAddress);
        return ResponseEntity.ok(BaseResponse.success("이미지가 삭제되었습니다.", null));
    }
}
