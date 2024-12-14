package com.follow_me.running_mate.domain.course.exception;

import com.follow_me.running_mate.global.error.code.ResultCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CourseErrorCode implements ResultCode {
    NOT_FOUND("COURSE001", "코스를 찾을 수 없습니다."),
    NOT_APPROVED("COURSE002", "코스가 승인되지 않았습니다."),
    ALREADY_BOOKMARKED("COURSE003", "이미 북마크한 코스입니다."),
    NOT_BOOKMARKED("COURSE004", "북마크한 코스가 아닙니다."),
    OVER_MAX_BOOKMARK("COURSE005", "북마크한 코스가 최대 개수를 초과했습니다."),
    DUPLICATED_NAME("COURSE006", "이미 존재하는 코스 이름입니다."),
    NOT_FOUND_REVIEW("COURSE007", "리뷰를 찾을 수 없습니다."),
    UNAUTHORIZED_REVIEW("COURSE008", "리뷰를 삭제할 권한이 없습니다."),
    ALREADY_APPROVED("COURSE009", "이미 승인된 코스입니다."),
    ERROR_COURSE_TO_JSON("COURSE010", "코스를 JSON으로 변환하는 중 오류가 발생했습니다."),
    ERROR_COURSE_S3_UPLOAD("COURSE011", "코스를 S3에 업로드하는 중 오류가 발생했습니다."),
    ERROR_LAMBDA_TO_BEDROCK("COURSE012", "Bedrock용 Lambda 함수 오류: "),
    ;

    private final String code;
    private final String message;
}
