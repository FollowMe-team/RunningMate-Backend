package com.follow_me.running_mate.domain.token.dto.response;

public record TokenResponse(
    String accessToken,
    String refreshToken
) {

}
