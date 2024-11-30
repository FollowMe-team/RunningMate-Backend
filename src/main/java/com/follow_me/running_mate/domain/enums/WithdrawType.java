package com.follow_me.running_mate.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WithdrawType {
    TOO_MANY_USE("너무 많이 사용함"),
    TOO_MANY_AD("광고가 너무 많음"),
    LOSE_RUNNING_INTEREST("러닝에 흥미를 잃음"),
    MEET_BAD_USER("비매너 이용자를 만남"),
    TO_MAKE_NEW_ACCOUNT("새로운 계정을 다시 만들고 싶음"),
    OTHER("기타"),
    ;

    private final String toKorean;
}
