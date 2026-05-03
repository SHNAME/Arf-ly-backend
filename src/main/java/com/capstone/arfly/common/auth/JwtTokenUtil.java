package com.capstone.arfly.common.auth;

import static java.util.Base64.getDecoder;

import com.capstone.arfly.common.constant.RedisConstant;
import com.capstone.arfly.common.exception.InvalidTokenException;
import com.capstone.arfly.common.exception.TokenExpiredException;
import com.capstone.arfly.member.domain.Member;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenUtil {
    private final long accessExpiration;
    private final SecretKey ACCESS_SECRET_KEY;
    private final long refreshExpiration;
    private final SecretKey REFRESH_SECRET_KEY;
    private final long passwordResetExpiration;
    private final SecretKey PASSWORD_RESET_KEY;
    private final RedisTemplate<String, String> redisTemplate;



    public JwtTokenUtil(@Value("${jwt.access-expiration}") long accessExpiration,
                        @Value("${jwt.access-secret}") String accessSecretKey
            , @Value("${jwt.refresh-expiration}") long refreshExpiration,
                        @Value("${jwt.refresh-secret}") String refreshSecretKey,
                        @Value("${jwt.password-reset-expiration}") long passwordResetExpiration,
                        @Value("${jwt.password-reset-secret}") String passwordResetSecretKey,
                        RedisTemplate<String, String> redisTemplate
    ) {
        this.accessExpiration = accessExpiration;
        this.refreshExpiration = refreshExpiration;
        this.passwordResetExpiration = passwordResetExpiration;
        this.REFRESH_SECRET_KEY = Keys.hmacShaKeyFor(getDecoder().decode(refreshSecretKey));
        this.ACCESS_SECRET_KEY = Keys.hmacShaKeyFor(getDecoder().decode(accessSecretKey));
        this.PASSWORD_RESET_KEY = Keys.hmacShaKeyFor(getDecoder().decode(passwordResetSecretKey));
        this.redisTemplate = redisTemplate;
    }

    public String createAccessToken(Long id, String role) {
        Date now = new Date();
        String accessToken = Jwts.builder().subject(String.valueOf(id)).claim("role", role)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + accessExpiration * 60 * 1000L))
                .signWith(ACCESS_SECRET_KEY)
                .compact();
        return accessToken;
    }

    public String createRefreshToken(Member member) {
        Date createdAt = new Date();
        Date expiredAt = new Date(createdAt.getTime() + refreshExpiration * 60 * 1000L);
        String refreshToken = Jwts.builder().subject(String.valueOf(member.getId()))
                .issuedAt(createdAt)
                .expiration(expiredAt)
                .signWith(REFRESH_SECRET_KEY)
                .compact();
        redisTemplate.opsForValue().set(RedisConstant.REFRESH_TOKEN_PREFIX + refreshToken,
                String.valueOf(member.getId()), refreshExpiration, TimeUnit.MINUTES);
        return refreshToken;
    }

    public void validateRefreshToken(String refreshToken) {
        try {
            Jwts.parser().verifyWith(REFRESH_SECRET_KEY).build().parseSignedClaims(refreshToken)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new TokenExpiredException();
        } catch (JwtException e) {
            throw new InvalidTokenException();
        }
    }

    public String createPasswordRestToken(Member member) {
        Date createdAt = new Date();
        Date expiredAt = new Date(createdAt.getTime() + passwordResetExpiration * 60 * 1000L);
        String passwordResetToken = Jwts.builder().subject(String.valueOf(member.getId()))
                .issuedAt(createdAt)
                .expiration(expiredAt)
                .signWith(PASSWORD_RESET_KEY)
                .compact();
        return passwordResetToken;
    }

    public Long validatePasswordResetToken(String passwordResetToken) {
        try {
            String id = Jwts.parser()
                    .verifyWith(PASSWORD_RESET_KEY)
                    .build()
                    .parseSignedClaims(passwordResetToken)
                    .getPayload()
                    .getSubject();
            return Long.parseLong(id);
        } catch (ExpiredJwtException e) {
            throw new TokenExpiredException();
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidTokenException();
        }
    }
}
