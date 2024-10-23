package com.follow_me.running_mate.global.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.follow_me.running_mate.global.error.code.ResultCode;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "API 응답 포맷")
public record BaseResponse<T>(
    @Schema(description = "응답 코드", example = "AUTH001")
    String code,

    @Schema(description = "응답 메시지")
    String message,

    @Schema(description = "응답 시간")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    LocalDateTime timestamp,

    @Schema(description = "응답 데이터")
    T data
) {

    public static <T> BaseResponse<T> success(String message, T data) {
        return new BaseResponse<>(
            CommonResultCode.SUCCESS.getCode(),
            message,
            LocalDateTime.now(),
            data
        );
    }

    public static <T> BaseResponse<T> error(ResultCode resultCode) {
        return new BaseResponse<>(
            resultCode.getCode(),
            resultCode.getMessage(),
            LocalDateTime.now(),
            null
        );
    }

    public static <T> BaseResponse<T> error(ResultCode resultCode, String message) {
        return new BaseResponse<>(
            resultCode.getCode(),
            message,
            LocalDateTime.now(),
            null
        );
    }

    public static <T> BaseResponse<T> error(ResultCode resultCode, T errors) {
        return new BaseResponse<>(
            resultCode.getCode(),
            resultCode.getMessage(),
            LocalDateTime.now(),
            errors
        );
    }
}
