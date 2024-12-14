package com.follow_me.running_mate.domain.course.service;

import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.follow_me.running_mate.domain.course.dto.request.CourseAnalysisRequest;
import com.follow_me.running_mate.domain.course.dto.response.CourseAnalysisResponse;
import com.follow_me.running_mate.domain.course.entity.Course;
import com.follow_me.running_mate.domain.course.entity.CoursePoint;
import com.follow_me.running_mate.domain.course.repository.CourseRepository;
import com.follow_me.running_mate.domain.course.service.option.CourseOptionService;
import com.follow_me.running_mate.domain.course.service.point.CoursePointService;
import com.follow_me.running_mate.domain.enums.CourseOptionType;
import com.follow_me.running_mate.domain.enums.CoursePointVoice;
import com.follow_me.running_mate.domain.enums.Difficulty;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
public class CourseAnalysisService {

    @Value("${cloud.aws.lambda.analysis}")
    private String lambdaName;

    private final AWSLambda awsLambda;
    private final ObjectMapper objectMapper;
    private final CourseRepository courseRepository;
    private final CourseOptionService courseOptionService;
    private final CoursePointService coursePointService;

    @Transactional
    public void invokeCourseDifficultyLambda(Course course, List<CoursePoint> points) {
        try {
            log.info("Lambda 요청 생성 시작: courseId={}", course.getId());
            CourseAnalysisRequest request = createRequest(course, points);
            log.info("Lambda 요청 데이터: {}", objectMapper.writeValueAsString(request));

            log.info("Lambda 함수 호출: courseId={}", course.getId());
            CourseAnalysisResponse response = invokeLambda(request);
            log.info("Lambda 응답 데이터: {}", objectMapper.writeValueAsString(response));

            log.info("Lambda 응답 처리 시작: courseId={}", course.getId());
            applyResponse(course, points, response);
            log.info("Lambda 처리 완료: courseId={}", course.getId());

        } catch (Exception e) {
            log.error("Lambda 함수 호출 중 오류 발생: courseId={}", course.getId(), e);
            throw new RuntimeException("코스 분석 중 오류가 발생했습니다", e);
        }
    }

    private CourseAnalysisRequest createRequest(Course course, List<CoursePoint> points) {
        List<CourseAnalysisRequest.PointInfo> pointInfos = points.stream()
            .map(point -> CourseAnalysisRequest.PointInfo.builder()
                .x(point.getLocation().getX())
                .y(point.getLocation().getY())
                .elevation(point.getElevation())
                .sequenceNumber(point.getSequenceNumber())
                .build())
            .toList();

        return CourseAnalysisRequest.builder()
            .totalDistance(course.getDistance())
            .points(pointInfos)
            .build();
    }

    private CourseAnalysisResponse invokeLambda(CourseAnalysisRequest request) throws JsonProcessingException {
        InvokeRequest invokeRequest = new InvokeRequest()
            .withFunctionName(lambdaName)
            .withPayload(objectMapper.writeValueAsString(request));

        InvokeResult result = awsLambda.invoke(invokeRequest);
        String responseStr = new String(result.getPayload().array());
        log.info("Lambda 원본 응답: {}", responseStr);  // 응답 로그 추가

        return objectMapper.readValue(responseStr, CourseAnalysisResponse.class);
    }


    private void applyResponse(Course course, List<CoursePoint> points, CourseAnalysisResponse response) {
        try {
            if (response == null) {
                log.error("Lambda 응답이 null입니다.");
                course.setDifficulty(Difficulty.NORMAL);  // 기본값 설정
                return;
            }

            String difficultyStr = response.getDifficulty();
            log.info("Lambda에서 받은 난이도: {}", difficultyStr);

            if (difficultyStr == null) {
                log.error("난이도 값이 null입니다.");
                course.setDifficulty(Difficulty.NORMAL);
            } else {
                try {
                    course.setDifficulty(Difficulty.valueOf(difficultyStr.trim().toUpperCase()));
                } catch (IllegalArgumentException e) {
                    log.error("잘못된 난이도 값: {}", difficultyStr);
                    course.setDifficulty(Difficulty.NORMAL);
                }
            }
            courseRepository.save(course);

            if (response.getGradient() != null) {
                courseOptionService.saveGradientOption(course, CourseOptionType.valueOf(response.getGradient()));
            }

            if (response.getVoicePoints() != null) {
                for (CourseAnalysisResponse.VoicePointInfo voicePoint : response.getVoicePoints()) {
                    if (voicePoint.getVoiceType() != null && voicePoint.getSequenceNumber() != null) {
                        points.stream()
                            .filter(p -> voicePoint.getSequenceNumber().equals(p.getSequenceNumber()))
                            .findFirst()
                            .ifPresent(point -> {
                                try {
                                    point.setVoice(CoursePointVoice.valueOf(voicePoint.getVoiceType().trim()));
                                    coursePointService.saveCoursePoint(point);
                                } catch (IllegalArgumentException e) {
                                    log.error("잘못된 voice type: {}", voicePoint.getVoiceType());
                                }
                            });
                    }
                }
            }
        } catch (Exception e) {
            log.error("응답 처리 중 예외 발생", e);
            course.setDifficulty(Difficulty.NORMAL);
            courseRepository.save(course);
        }
    }
}