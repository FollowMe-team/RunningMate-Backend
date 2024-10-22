package com.follow_me.running_mate.global.error.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ValidationErrorCode implements ResultCode {
    // 기본 유효성 검사 실패
    INVALID_INPUT("VALID001", "잘못된 입력값입니다."),

    // 타입 관련
    INVALID_TYPE("VALID002", "잘못된 타입입니다."),
    TYPE_MISMATCH("VALID003", "타입이 일치하지 않습니다."),

    // 필수값 관련
    MISSING_PARAMETER("VALID004", "필수 파라미터가 누락되었습니다."),
    MISSING_REQUEST_BODY("VALID005", "요청 바디가 누락되었습니다."),

    // 형식 관련
    INVALID_FORMAT("VALID006", "잘못된 형식입니다."),
    MALFORMED_JSON("VALID007", "잘못된 JSON 형식입니다."),

    // 값 범위 관련
    OUT_OF_RANGE("VALID008", "허용된 범위를 벗어난 값입니다."),

    // 기타
    METHOD_NOT_SUPPORTED("VALID009", "지원하지 않는 HTTP 메서드입니다."),
    MEDIA_TYPE_NOT_SUPPORTED("VALID010", "지원하지 않는 미디어 타입입니다.");

    private final String code;
    private final String message;
}
