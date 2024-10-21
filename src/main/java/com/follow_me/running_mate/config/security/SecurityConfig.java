package com.follow_me.running_mate.config.security;

import com.follow_me.running_mate.config.security.jwt.JwtAuthenticationFilter;
import com.follow_me.running_mate.config.security.jwt.JwtAuthorizationFilter;
import com.follow_me.running_mate.config.security.jwt.JwtTokenProvider;
import com.follow_me.running_mate.config.security.jwt.RefreshTokenFilter;
import com.follow_me.running_mate.domain.token.repository.TokenRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final TokenRepository tokenRepository;

    public SecurityConfig(JwtTokenProvider jwtTokenProvider, AuthenticationConfiguration authenticationConfiguration, TokenRepository tokenRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationConfiguration = authenticationConfiguration;
        this.tokenRepository = tokenRepository;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .httpBasic(AbstractHttpConfigurer::disable)
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(
                session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/api/auth/signup", "/api/auth/login", "api/auth/refresh").permitAll()
                .anyRequest().authenticated()
            )
            .addFilter(new JwtAuthenticationFilter(authenticationManager(authenticationConfiguration), jwtTokenProvider))
            .addFilterBefore(new JwtAuthorizationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(new RefreshTokenFilter(jwtTokenProvider, tokenRepository), JwtAuthorizationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}