package com.follow_me.running_mate.global.common.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.IOUtils;
import com.follow_me.running_mate.global.error.code.CommonErrorCode;
import com.follow_me.running_mate.global.error.exception.CustomException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Component
public class S3ImageService {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public String upload(MultipartFile image) {
        if(image.isEmpty() || Objects.isNull(image.getOriginalFilename())){
            throw new CustomException(CommonErrorCode.FILE_NOT_FOUND);
        }
        //S3 에 저장된 URL 반환
        return this.uploadImage(image);
    }

    private String uploadImage(MultipartFile image) {
        this.validateImageFileExtention(image.getOriginalFilename());
        try {
            return this.uploadImageToS3(image);
        } catch (IOException e) {
            throw new CustomException(CommonErrorCode.FILE_UPLOAD_ERROR, "이미지 업로드 중 IO 오류가 발생했습니다.");
        }
    }

    private void validateImageFileExtention(String filename) {
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex == -1) {
            throw new CustomException(CommonErrorCode.INVALID_TYPE_VALUE , "파일 확장자가 존재하지 않습니다.");
        }

        String extention = filename.substring(lastDotIndex + 1).toLowerCase();
        List<String> allowedExtentionList = Arrays.asList("jpg", "jpeg", "png", "gif");

        if (!allowedExtentionList.contains(extention)) {
            throw new CustomException(CommonErrorCode.INVALID_FILE_TYPE , "지원하지 않는 파일 형식입니다.");
        }
    }

    private String uploadImageToS3(MultipartFile image) throws IOException {
        String originalFilename = image.getOriginalFilename(); // 원본 파일명
        int lastDotIndex = originalFilename.lastIndexOf(".");
        if (lastDotIndex == -1) {
            throw new CustomException(CommonErrorCode.INVALID_TYPE_VALUE, "파일 확장자가 존재하지 않습니다.");
        }

        String extention = originalFilename.substring(lastDotIndex + 1).toLowerCase(); // 확장자만 추출 (점 제거)
        if (!Arrays.asList("jpg", "jpeg", "png", "gif").contains(extention)) {
            throw new CustomException(CommonErrorCode.INVALID_FILE_TYPE, "지원하지 않는 파일 형식입니다.");
        }

        String s3FileName = UUID.randomUUID().toString().substring(0, 10) + "." + extention; // 새로운 파일명 생성

        InputStream is = image.getInputStream();
        byte[] bytes = IOUtils.toByteArray(is); // 이미지 byte 배열로 변환

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("image/" + extention); // 올바른 Content-Type 설정
        metadata.setContentLength(bytes.length);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);

        try {
            PutObjectRequest putObjectRequest =
                new PutObjectRequest(bucketName, s3FileName, byteArrayInputStream, metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead);
            amazonS3.putObject(putObjectRequest); // S3에 이미지 업로드
        } catch (Exception e) {
            throw new CustomException(CommonErrorCode.FILE_UPLOAD_ERROR, "S3에 이미지 업로드 중 오류 발생");
        } finally {
            byteArrayInputStream.close();
            is.close();
        }

        return amazonS3.getUrl(bucketName, s3FileName).toString();
    }

    public void deleteImageFromS3(String imageAddress){
        String key = getKeyFromImageAddress(imageAddress);
        try{
            amazonS3.deleteObject(new DeleteObjectRequest(bucketName, key));
        }catch (Exception e){
            throw new CustomException(CommonErrorCode.FILE_NOT_FOUND, "S3에서 이미지 삭제 중 오류 발생");
        }
    }

    private String getKeyFromImageAddress(String imageAddress){
        try{
            URL url = new URL(imageAddress);
            String decodingKey = URLDecoder.decode(url.getPath(), "UTF-8");
            return decodingKey.substring(1);
        }catch (MalformedURLException | UnsupportedEncodingException e){
            throw new CustomException(CommonErrorCode.INVALID_INPUT_VALUE, "이미지 주소 파싱 중 오류 발생");
        }
    }
}