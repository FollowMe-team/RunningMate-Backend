package com.follow_me.running_mate.domain.course.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.follow_me.running_mate.domain.course.dto.request.CourseS3Request;
import com.follow_me.running_mate.domain.course.entity.Course;
import com.follow_me.running_mate.domain.course.entity.CourseOption;
import com.follow_me.running_mate.domain.course.exception.CourseErrorCode;
import com.follow_me.running_mate.global.error.exception.CustomException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class S3CourseService {

    private final AmazonS3 amazonS3;
    private String bucketName;

    @Autowired
    public S3CourseService(
        @Qualifier("amazonS3Course") AmazonS3 amazonS3,
        @Value("${cloud.aws.s3.bucket.course}") String bucketName
    ) {
        this.amazonS3 = amazonS3;
        this.bucketName = bucketName;
    }


    public void saveCourseToS3(Course course) {

        ObjectMapper objectMapper = new ObjectMapper();
        String courseJson;
        try {
            courseJson = objectMapper.writeValueAsString(toCourseS3Request(course));
        } catch (IOException e) {
            throw new CustomException(CourseErrorCode.ERROR_COURSE_TO_JSON);
        }

        String fileName = "course-" + course.getId() + ".json";

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("application/json");
        metadata.setContentLength(courseJson.getBytes(StandardCharsets.UTF_8).length);

        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(courseJson.getBytes(StandardCharsets.UTF_8))) {
            amazonS3.putObject(bucketName, fileName, byteArrayInputStream, metadata);
        } catch (Exception e) {
            throw new CustomException(
                CourseErrorCode.ERROR_COURSE_S3_UPLOAD,
                CourseErrorCode.ERROR_COURSE_S3_UPLOAD.getMessage() + ": " + e.getMessage()
            );
        }
    }

    private CourseS3Request toCourseS3Request(Course course) {

        long seconds = course.getDuration().getSeconds();

        return CourseS3Request.builder()
            .id(course.getId())
            .name(course.getName())
            .description(course.getDescription())
            .startLatitude(course.getStartPoint().getY())
            .startLongitude(course.getStartPoint().getX())
            .distance(course.getDistance())
            .duration(seconds)
            .difficulty(course.getDifficulty().name())
            .options(course.getOptions().stream()
                .map(option -> option.getType().name())
                .toList())
            .build();
    }
}
