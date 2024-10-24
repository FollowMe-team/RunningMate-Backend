package com.follow_me.running_mate.config.security.jwt;

import static com.follow_me.running_mate.config.security.jwt.JwtConstant.ACCESS_TOKEN_EXPIRE_TIME;
import static com.follow_me.running_mate.config.security.jwt.JwtConstant.AUTHORIZATION_HEADER;
import static com.follow_me.running_mate.config.security.jwt.JwtConstant.BEARER_PREFIX;
import static com.follow_me.running_mate.config.security.jwt.JwtConstant.REFRESH_TOKEN_EXPIRE_TIME;

import com.follow_me.running_mate.config.security.auth.PrincipalDetailsService;
import com.follow_me.running_mate.domain.member.exception.AuthErrorCode;
import com.follow_me.running_mate.domain.token.dto.response.TokenResponse;
import com.follow_me.running_mate.domain.token.entity.Token;
import com.follow_me.running_mate.domain.token.repository.TokenRepository;
import com.follow_me.running_mate.global.error.exception.CustomException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${spring.security.secret-key}")
    private String secretKey;

    private final TokenRepository tokenRepository;
    private final PrincipalDetailsService principalDetailsService;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public TokenResponse createToken(String email, String role) {
        String accessToken = createAccessToken(email, role);
        String refreshToken = createRefreshToken(email);
        return new TokenResponse(accessToken, refreshToken);
    }

    // Access Token 생성
    private String createAccessToken(String email, String role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_TIME);

        return Jwts.builder()
            .setSubject(email)
            .claim("role", role)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact();
    }

    // Refresh Token 생성 및 Redis 저장
    private String createRefreshToken(String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + REFRESH_TOKEN_EXPIRE_TIME);

        String refreshToken = Jwts.builder()
            .setSubject(email)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact();

        tokenRepository.save(new Token(email, refreshToken));

        return refreshToken;
    }

    // JWT 토큰에서 인증 정보 조회
    public Authentication getAuthentication(String token) {
        String email = getEmailFromToken(token);
        UserDetails userDetails = principalDetailsService.loadUserByUsername(email);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getEmailFromToken(String token) {
        return Jwts.parser()
            .setSigningKey(secretKey)
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            throw new CustomException(AuthErrorCode.INVALID_TOKEN);
        } catch (UnsupportedJwtException e) {
            throw new CustomException(AuthErrorCode.UNSUPPORTED_TOKEN);
        } catch (IllegalArgumentException e) {
            throw new CustomException(AuthErrorCode.INVALID_TOKEN_FORMAT);
        } catch (ExpiredJwtException e) {
            throw new CustomException(AuthErrorCode.EXPIRED_TOKEN);
        }
    }

    // 헤더에서 토큰 추출
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (bearerToken != null && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }

    // Refresh Token 만료 처리 (Refresh Token 삭제)
    public void invalidateRefreshToken(String email) {
        tokenRepository.deleteById(email);
    }
}
