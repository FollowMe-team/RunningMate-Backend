package com.follow_me.running_mate.global.error.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements ResultCode {
    // 일반적인 성공/실패 응답
    INVALID_INPUT_VALUE("COMMON001", "잘못된 입력값입니다."),
    INVALID_TYPE_VALUE("COMMON002", "잘못된 타입입니다."),
    MISSING_INPUT_VALUE("COMMON003", "필수 입력값이 누락되었습니다."),
    NOT_FOUND("COMMON004", "리소스를 찾을 수 없습니다."),
    METHOD_NOT_ALLOWED("COMMON005", "지원하지 않는 HTTP Method입니다."),

    // API 관련
    API_NOT_FOUND("COMMON006", "API를 찾을 수 없습니다."),
    API_ERROR("COMMON007", "API 호출 중 오류가 발생했습니다."),

    // 권한 관련
    UNAUTHORIZED("COMMON008", "인증이 필요합니다."),
    ACCESS_DENIED("COMMON009", "접근이 거부되었습니다."),

    // 서버 관련
    INTERNAL_SERVER_ERROR("COMMON010", "서버 내부 오류가 발생했습니다."),
    DATABASE_ERROR("COMMON011", "데이터베이스 오류가 발생했습니다."),

    // 파일 관련
    FILE_NOT_FOUND("COMMON012", "파일을 찾을 수 없습니다."),
    FILE_UPLOAD_ERROR("COMMON013", "파일 업로드에 실패했습니다."),
    INVALID_FILE_TYPE("COMMON014", "지원하지 않는 파일 형식입니다."),
    FILE_SIZE_EXCEED("COMMON015", "파일 크기가 제한을 초과했습니다."),

    // 외부 서비스 연동 관련
    EXTERNAL_SERVICE_ERROR("COMMON016", "외부 서비스 연동 중 오류가 발생했습니다."),
    TIMEOUT("COMMON017", "요청 시간이 초과되었습니다.");

    private final String code;
    private final String message;
}