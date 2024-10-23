package com.follow_me.running_mate.config.security;

import java.util.Arrays;
import java.util.stream.Stream;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityConstant {
    // Auth 관련 공개 API 경로
    public static final String[] PUBLIC_AUTH_URLS = {
        "/api/auth/signup",
        "/api/auth/login",
        "/api/auth/refresh"
    };

    // Swagger UI 관련 공개 경로
    public static final String[] SWAGGER_URLS = {
        "/v3/api-docs/**",
        "/swagger-ui/**",
        "/swagger-ui.html"
    };

    // 모든 공개 URL들
    public static final String[] PUBLIC_URLS =
        Stream.of(PUBLIC_AUTH_URLS, SWAGGER_URLS)
            .flatMap(Arrays::stream)
            .toArray(String[]::new);
}
