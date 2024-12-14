package com.follow_me.running_mate.domain.course.service;

import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.follow_me.running_mate.domain.course.dto.request.CourseRecommendRequest;
import com.follow_me.running_mate.domain.course.dto.response.CourseRecommendResponse;
import com.follow_me.running_mate.domain.course.exception.CourseErrorCode;
import com.follow_me.running_mate.domain.enums.Difficulty;
import com.follow_me.running_mate.domain.enums.Ranking;
import com.follow_me.running_mate.domain.enums.RunningGoal;
import com.follow_me.running_mate.domain.member.entity.Member;
import com.follow_me.running_mate.global.error.exception.CustomException;
import java.nio.charset.StandardCharsets;
import java.util.List;
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

    public List<Long> invokeLambda(
        Member member, Double latitude, Double longitude, Difficulty difficulty, RunningGoal runningGoal
    ) {
        log.info("Lambda 호출 시작: Member={}, Latitude={}, Longitude={}, Difficulty={}, RunningGoal={}",
            member, latitude, longitude, difficulty, runningGoal);

        try {
            // Lambda 요청 생성
            CourseRecommendRequest request = toCourseRecommendRequest(member, latitude, longitude, difficulty, runningGoal);
            String requestJson = objectMapper.writeValueAsString(request);
            log.info("Lambda 요청 데이터(JSON): {}", requestJson);

            InvokeRequest invokeRequest = new InvokeRequest()
                .withFunctionName(lambdaName)
                .withPayload(requestJson);

            // Lambda 호출
            log.info("Lambda 호출: 함수 이름={}, 요청 데이터 길이={}", lambdaName, requestJson.length());
            InvokeResult result = awsLambda.invoke(invokeRequest);

            // 응답 처리
            String responseJson = new String(result.getPayload().array(), StandardCharsets.UTF_8);
            log.info("Lambda 응답 데이터(JSON): {}", responseJson);

            CourseRecommendResponse response = objectMapper.readValue(responseJson, CourseRecommendResponse.class);
            log.info("Lambda 응답 객체: {}", response);

            if (response.getStatusCode() != 200) {
                throw new CustomException(
                    CourseErrorCode.ERROR_LAMBDA_TO_BEDROCK,
                    CourseErrorCode.ERROR_LAMBDA_TO_BEDROCK.getMessage() + response.getBody()
                );
            }

            if (response.getBody() == null) {
                return List.of();
            }

            return response.getBody().getCourseIds();

        } catch (Exception e) {
            log.error("Lambda 호출 중 오류 발생: {}", e.getMessage(), e);
            throw new CustomException(
                CourseErrorCode.ERROR_LAMBDA_TO_BEDROCK,
                CourseErrorCode.ERROR_LAMBDA_TO_BEDROCK.getMessage() + e.getMessage()
            );
        }
    }

    private CourseRecommendRequest toCourseRecommendRequest(
        Member member, Double latitude, Double longitude, Difficulty difficulty, RunningGoal runningGoal
    ) {
        log.info("CourseRecommendRequest 생성 시작: Member={}, Latitude={}, Longitude={}, Difficulty={}, RunningGoal={}",
            member, latitude, longitude, difficulty, runningGoal);

        Ranking ranking = member.getRanking();

        CourseRecommendRequest request = CourseRecommendRequest.builder()
            .latitude(latitude)
            .longitude(longitude)
            .difficulty(difficulty)
            .goal((runningGoal != null) ? runningGoal.getToKorean() : null)
            .rank(ranking.getName() + "(" + ranking.getCriteria() + ")")
            .build();

        log.info("CourseRecommendRequest 생성 완료: {}", request);
        return request;
    }
}
