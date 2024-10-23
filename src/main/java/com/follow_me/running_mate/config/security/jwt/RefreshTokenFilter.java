package com.follow_me.running_mate.config.security.jwt;

import static com.follow_me.running_mate.config.security.jwt.JwtConstant.REFRESH_TOKEN_HEADER;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.follow_me.running_mate.config.security.auth.PrincipalDetails;
import com.follow_me.running_mate.domain.member.exception.AuthErrorCode;
import com.follow_me.running_mate.domain.token.dto.response.TokenResponse;
import com.follow_me.running_mate.domain.token.entity.Token;
import com.follow_me.running_mate.domain.token.repository.TokenRepository;
import com.follow_me.running_mate.global.common.BaseResponse;
import com.follow_me.running_mate.global.error.code.CommonErrorCode;
import com.follow_me.running_mate.global.error.exception.CustomException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import org.springframework.web.filter.OncePerRequestFilter;

public class RefreshTokenFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final TokenRepository tokenRepository;

    public RefreshTokenFilter(JwtTokenProvider jwtTokenProvider, TokenRepository tokenRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.tokenRepository = tokenRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        String refreshToken = request.getHeader(REFRESH_TOKEN_HEADER);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"));

        try {
            if (refreshToken != null) {
                if (!jwtTokenProvider.validateToken(refreshToken)) {
                    throw new CustomException(AuthErrorCode.INVALID_TOKEN);
                }

                String email = jwtTokenProvider.getEmailFromToken(refreshToken);
                Token storedToken = tokenRepository.findById(email)
                    .orElseThrow(() -> new CustomException(AuthErrorCode.REFRESH_TOKEN_NOT_FOUND));

                if (!storedToken.getTokenValue().trim().equals(refreshToken.trim())) {
                    throw new CustomException(AuthErrorCode.REFRESH_TOKEN_MISMATCH);
                }

                PrincipalDetails principalDetails = (PrincipalDetails) jwtTokenProvider.getAuthentication(refreshToken).getPrincipal();
                TokenResponse tokenResponse = jwtTokenProvider.createToken(
                    principalDetails.getUsername(), principalDetails.member().getRole().name()
                );

                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(objectMapper.writeValueAsString(
                    BaseResponse.success("토큰이 갱신되었습니다.", tokenResponse)
                ));
                return;
            }
        } catch (CustomException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(
                BaseResponse.error(e.getResultCode(), e.getMessage())
            ));
            return;
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(
                BaseResponse.error(CommonErrorCode.INTERNAL_SERVER_ERROR, e.getMessage())
            ));
            return;
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !"/api/auth/refresh".equals(request.getRequestURI());
    }
}
