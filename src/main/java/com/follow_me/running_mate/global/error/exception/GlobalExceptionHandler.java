package com.follow_me.running_mate.global.error.exception;

import com.follow_me.running_mate.global.common.ApiResponse;
import com.follow_me.running_mate.global.error.code.CommonErrorCode;
import com.follow_me.running_mate.global.error.code.ValidationErrorCode;
import com.follow_me.running_mate.global.error.response.ValidationError;
import com.follow_me.running_mate.global.error.response.ValidationErrors;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class GlobalExceptionHandler {

    // CustomException 처리
    @ExceptionHandler(CustomException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleCustomException(CustomException e) {
        log.error("CustomException: {}", e.getMessage());
        return ApiResponse.error(e.getResultCode(), e.getMessage());
    }

    // ValidationException 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<ValidationErrors> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<ValidationError> errors = e.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> new ValidationError(
                error.getField(),
                error.getDefaultMessage()
            ))
            .toList();

        return ApiResponse.error(
            ValidationErrorCode.INVALID_INPUT,
            new ValidationErrors(errors)
        );
    }

    // JSON 파싱 실패
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        log.error("HttpMessageNotReadableException: {}", e.getMessage());
        return ApiResponse.error(ValidationErrorCode.MALFORMED_JSON);
    }

    // 타입 변환 실패
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException e) {
        log.error("MethodArgumentTypeMismatchException: {}", e.getMessage());
        return ApiResponse.error(ValidationErrorCode.TYPE_MISMATCH);
    }

    // 필수 파라미터 누락
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleMissingServletRequestParameter(MissingServletRequestParameterException e) {
        log.error("MissingServletRequestParameterException: {}", e.getMessage());
        return ApiResponse.error(ValidationErrorCode.MISSING_PARAMETER);
    }

    // 지원하지 않는 HTTP 메서드
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ApiResponse<Void> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        log.error("HttpRequestMethodNotSupportedException: {}", e.getMessage());
        return ApiResponse.error(ValidationErrorCode.METHOD_NOT_SUPPORTED);
    }

    // 지원하지 않는 미디어 타입
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    public ApiResponse<Void> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException e) {
        log.error("HttpMediaTypeNotSupportedException: {}", e.getMessage());
        return ApiResponse.error(ValidationErrorCode.MEDIA_TYPE_NOT_SUPPORTED);
    }

    // 기타 서버 에러
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleException(Exception e) {
        log.error("Exception: {}", e.getMessage());
        return ApiResponse.error(CommonErrorCode.INTERNAL_SERVER_ERROR);
    }
}
