package com.follow_me.running_mate.domain.course.service;

import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.follow_me.running_mate.domain.course.dto.request.CourseRecommendRequest;
import com.follow_me.running_mate.domain.course.dto.response.CourseRecommendResponse;
import com.follow_me.running_mate.domain.course.exception.CourseErrorCode;
import com.follow_me.running_mate.domain.enums.Difficulty;
import com.follow_me.running_mate.domain.enums.RunningGoal;
import com.follow_me.running_mate.domain.member.entity.Member;
import com.follow_me.running_mate.global.error.exception.CustomException;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CourseRecommendService {

    private final AWSLambda awsLambda;
    private final String lambdaName;
    private final ObjectMapper objectMapper;

    @Autowired
    public CourseRecommendService(
        @Qualifier("awsLambdaToBedrock") AWSLambda awsLambda,
        @Value("${cloud.aws.lambda.recommend}") String lambdaName
    ) {
        this.awsLambda = awsLambda;
        this.lambdaName = lambdaName;
        this.objectMapper = new ObjectMapper();
    }

    public CourseRecommendResponse invokeLambda(
        Member member, Double latitude, Double longitude, Difficulty difficulty, RunningGoal runningGoal
    ) {
        try {
            // Lambda 요청 생성
            InvokeRequest invokeRequest = new InvokeRequest()
                .withFunctionName(lambdaName)
                .withPayload(objectMapper.writeValueAsString(
                    toCourseRecommendRequest(member, latitude, longitude, difficulty, runningGoal)
                ));

            // Lambda 호출
            InvokeResult result = awsLambda.invoke(invokeRequest);

            // 응답 처리
            String responseJson = new String(result.getPayload().array(), StandardCharsets.UTF_8);
            return objectMapper.readValue(responseJson, CourseRecommendResponse.class);

        } catch (Exception e) {
            throw new CustomException(
                CourseErrorCode.ERROR_LAMBDA_TO_BEDROCK,
                CourseErrorCode.ERROR_LAMBDA_TO_BEDROCK.getMessage() + e.getMessage()
            );
        }
    }

    private CourseRecommendRequest toCourseRecommendRequest(
        Member member, Double latitude, Double longitude, Difficulty difficulty, RunningGoal runningGoal
    ) {

        return CourseRecommendRequest.builder()
            .latitude(latitude)
            .longitude(longitude)
            .difficulty(difficulty)
            .goal((runningGoal != null) ? runningGoal.getToKorean() : null)
            .rank(member.getRanking())
            .build();
    }
}
