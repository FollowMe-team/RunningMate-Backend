package com.follow_me.running_mate.domain.course.service;

import org.springframework.stereotype.Service;

@Service
public class LambdaService {
//    private final AWSLambda awsLambda;
//    private final ObjectMapper objectMapper;
//
//    public LambdaService(AWSLambda awsLambda, ObjectMapper objectMapper) {
//        this.awsLambda = awsLambda;
//        this.objectMapper = objectMapper;
//    }
//
//    // 람다 호출 함수의 틀
//    public void invokeCourseDifficultyLambda(Long courseId) {
//        try {
//            // 1. Payload 준비
//            Map<String, Object> payload = Map.of("courseId", courseId);
//            String payloadJson = objectMapper.writeValueAsString(payload);
//
//            // 2. InvokeRequest 설정
//            InvokeRequest invokeRequest = new InvokeRequest()
//                .withFunctionName("CourseDifficultyCalculationLambda") // 람다 함수명
//                .withPayload(payloadJson);
//
//            // 3. 람다 호출
//            InvokeResult result = awsLambda.invoke(invokeRequest);
//
//            // 결과 처리 (로그 출력 등)
//            String response = new String(result.getPayload().array());
//            System.out.println("Lambda response: " + response);
//
//        } catch (Exception e) {
//            // 예외 처리 (예: 로그 출력)
//            System.err.println("Lambda invocation failed: " + e.getMessage());
//        }
//    }
}
