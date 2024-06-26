package com.example.speed_you_back.security;

import com.example.speed_you_back.dto.ProfileDto;
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

    // 생성자
    public JwtUtil(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.expiration_time}") long accessTokenExpTime
    ) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenExpTime = accessTokenExpTime;
    }

    // Access Token 생성
    public String createAccessToken(ProfileDto.Profile profile) {
        return createToken(profile, accessTokenExpTime);
    }

    // JWT 생성
    private String createToken(ProfileDto.Profile profile, long expireTime)
    {
        Claims claims = Jwts.claims();
        claims.put("memberId", profile.getProfile_id());
        claims.put("email", profile.getEmail());
        claims.put("role", profile.getRole());

        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime tokenValidity = now.plusSeconds(expireTime);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(Date.from(now.toInstant()))
                .setExpiration(Date.from(tokenValidity.toInstant()))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // token에서 profile_id 추출
    public Long getUserId(String token) {
        return parseClaims(token).get("memberId", Long.class);
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
    public Claims parseClaims(String accessToken)
    {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}
