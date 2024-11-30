package com.follow_me.running_mate.domain.crew.exception;

import com.follow_me.running_mate.global.error.code.ResultCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CrewErrorCode implements ResultCode {
    NOT_FOUND("CREW001", "크루를 찾을 수 없습니다."),
    ALREADY_EXISTS("CREW002", "이미 신청중입니다."),
    SCHEDULE_CONFLICT("CREW003", "해당 시간에 이미 등록된 일정이 있습니다"),
    FORBIDDEN_ACCESS("CREW004", "해당 사용자에게 권한이 없습니다."),
    SCHEDULE_FULL("CREW005","인원을 초과했습니다." ),
    INVALID_INPUT_VALUE("CREW006","유효하지 않은 상태 값입니다." ),
    NOAPPLY_CREW("CREW007", "해당 크루에 신청하지 않은 사용자입니다."),
    DUPLICATE_RESOURCE("CREW008","이미 즐겨찾기된 코스입니다." ),
    NOT_FOUND_SCHEDULE("CREW009", "해당 스케줄을 찾을 수 없습니다."),
    INVALID_MEMBER_COUNT("CREW010", "크루 참여 인원이 음수입니다."),
    APPLY_NOT_FOUND("CREW011", "해당 일정에 신청한 적 없는 사용자입니다."),
    ALREADY_CANCELLED("CREW012", "이미 취소한 신청입니다."),
    CREW_LEADER("CREW013", "해당 크루의 크루장입니다. 크루장을 변경하고 다시 시도해주세요"),
    NOT_FOUND_CREWCOURSE("CREW014", "해당 코스는 즐겨찾기에 존재하지 않습니다.");

    private final String code;
    private final String message;
}
