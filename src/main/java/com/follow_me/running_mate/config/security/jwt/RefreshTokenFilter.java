package com.follow_me.running_mate.config.security.jwt;

import static com.follow_me.running_mate.config.security.jwt.JwtConstant.REFRESH_TOKEN_HEADER;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.follow_me.running_mate.config.security.auth.PrincipalDetails;
import com.follow_me.running_mate.domain.token.dto.response.TokenResponse;
import com.follow_me.running_mate.domain.token.entity.Token;
import com.follow_me.running_mate.domain.token.repository.TokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
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

        if (refreshToken != null && jwtTokenProvider.validateToken(refreshToken)) {
            String email = jwtTokenProvider.getEmailFromToken(refreshToken);
            Token storedToken = tokenRepository.findById(email).orElse(null);

            if (storedToken != null) {
                if (storedToken.getTokenValue().trim().equals(refreshToken.trim())) {
                    PrincipalDetails principalDetails
                        = (PrincipalDetails) jwtTokenProvider.getAuthentication(refreshToken).getPrincipal();
                    TokenResponse tokenResponse = jwtTokenProvider.createToken(
                        principalDetails.getUsername(), principalDetails.member().getRole().name()
                    );

                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write(new ObjectMapper().writeValueAsString(tokenResponse));
                    return;
                } else {
                    // 토큰이 일치하지 않는 경우 로그 찍기 (디버깅용)
                    System.out.println("Stored token: " + storedToken.getTokenValue());
                    System.out.println("Provided token: " + refreshToken);
                }
            }
            // TODO: 재로그인 요청
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !"/api/auth/refresh".equals(request.getRequestURI());
    }
}
