package com.lightningbid.auth.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

    private final SecretKey secretKey;

    private final Long accessTokenExpirationMillis;

    private final Long refreshTokenExpirationMillis;

    public JwtUtil(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.access-token-expiration-millis}") Long accessTokenExpirationMillis,
            @Value("${jwt.refresh-token-expiration-millis}") Long refreshTokenExpirationMillis) {

        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        this.accessTokenExpirationMillis = accessTokenExpirationMillis;
        this.refreshTokenExpirationMillis = refreshTokenExpirationMillis;
    }

    public String createAccessToken(Long id, String username, String role, String nickname, String profileUrl) {
        return createToken(id, username, role, nickname, profileUrl, accessTokenExpirationMillis);
    }

    public String createRefreshToken(String username, String role) {
        return createToken(null, username, role, null, null, refreshTokenExpirationMillis);
    }

    // 소셜 로그인 후 추가 정보 입력을 위한 임시 토큰 생성.
    public String createSignupToken(String username, String role) {
        long validityInMilliseconds = 10 * 60 * 1000L;
        return createToken(null, username, role, null, null, validityInMilliseconds);
    }

    private String createToken(Long id, String username, String role, String nickname, String profileUrl, Long expirationMillis) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMillis);

        return Jwts.builder()
                .subject(username)
                .claim("id", id)
                .claim("nickname", nickname)
                .claim("profileUrl", profileUrl)
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    // 토큰의 유효성을 검증하고, 실패 시 예외 던진다.
    public void verifyToken(String token) {
        Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token);
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String getUsername(String token) {
        return getClaims(token).getSubject();
    }

    public String getRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    public String getNickname(String token) {
        return getClaims(token).get("nickname", String.class);
    }

    public String getProfileUrl(String token) {
        return getClaims(token).get("profileUrl", String.class);
    }
}
