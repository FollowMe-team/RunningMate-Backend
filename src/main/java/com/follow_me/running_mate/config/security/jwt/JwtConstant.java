package com.follow_me.running_mate.config.security.jwt;

public class JwtConstant {

    public static final String BEARER_PREFIX = "Bearer ";
    public static final String AUTHORIZATION_HEADER = "Authorization";

    public static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 12;
    public static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 14;
}

