package com.follow_me.running_mate.domain.token.entity;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "Token", timeToLive = 604800) // 7일
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class Token {
    @Id
    private String id;
    private String tokenValue;
}