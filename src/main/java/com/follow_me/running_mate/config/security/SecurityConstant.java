package com.follow_me.running_mate.config.security;

import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.stream.Stream;

@Configuration
public class SecurityConstant {
    // Auth 관련 공개 API 경로
    public static final String[] PUBLIC_AUTH_URLS = {
            "/api/auth/signup",
            "/api/auth/login",
            "/api/auth/refresh",
            "/api/images",
            "/api/images/delete",
            "/api/members/check/**",
            "/api/crew/check/**",
    };

    // Swagger UI 관련 공개 경로
    public static final String[] SWAGGER_URLS = {
        "/v3/api-docs/**",
        "/swagger-ui/**",
        "/swagger-ui.html"
    };

    // Admin 관련 API 경로
    public static final String[] ADMIN_URLS = {
        "/api/courses/*/approve",
    };

    // 모든 공개 URL들
    public static final String[] PUBLIC_URLS =
        Stream.of(PUBLIC_AUTH_URLS, SWAGGER_URLS)
            .flatMap(Arrays::stream)
            .toArray(String[]::new);
}
