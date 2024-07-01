package com.example.speed_you_back.security;

import com.example.speed_you_back.dto.ProfileDto;
import com.example.speed_you_back.dto.TokenDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.ZonedDateTime;
import java.util.Date;

/* JWT 관련 메서드 제공 클래스 */

@Component
@Slf4j
public class JwtUtil
{
    private final Key key;
    private final long accessTokenExpTime;
    private final long refreshTokenExpTime;

    // 생성자
    public JwtUtil(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.access_expiration_time}") long accessTokenExpTime,
            @Value("${jwt.refresh_expiration_time}") long refreshTokenExpTime
    ) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenExpTime = accessTokenExpTime;
        this.refreshTokenExpTime = refreshTokenExpTime;
    }

    // Token 생성 및 반환
    public TokenDto returnToken(ProfileDto.Profile profile, boolean rememberMe) {
        return createToken(profile, accessTokenExpTime, refreshTokenExpTime, rememberMe);
    }

    // Token 생성
    private TokenDto createToken(ProfileDto.Profile profile, long accessTokenExpTime, long refreshTokenExpTime, boolean rememberMe)
    {
        Claims claims = Jwts.claims();
        claims.put("profile_id", profile.getProfile_id());
        claims.put("email", profile.getEmail());
        claims.put("role", profile.getRole());

        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime accessTokenValidity = now.plusSeconds(accessTokenExpTime);
        ZonedDateTime refreshTokenValidity = now.plusSeconds(refreshTokenExpTime);

        // access token
        String accessToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(Date.from(now.toInstant()))
                .setExpiration(Date.from(accessTokenValidity.toInstant()))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        if(!rememberMe) {
            return TokenDto.builder()
                    .accessToken(accessToken)
                    .build();
        }
        else {

            // refresh token
            String refreshToken = Jwts.builder()
                    .setClaims(claims)
                    .setIssuedAt(Date.from(now.toInstant()))
                    .setExpiration(Date.from(refreshTokenValidity.toInstant()))
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();

            return TokenDto.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
        }
    }

    // token에서 profile_id 추출
    public Long getProfileId(String token) {
        return parseClaims(token).get("profile_id", Long.class);
    }

    // JWT 검증
    public boolean validateToken(String token)
    {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        }

        return false;
    }

    // JWT claims 추출
    public Claims parseClaims(String token)
    {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}
