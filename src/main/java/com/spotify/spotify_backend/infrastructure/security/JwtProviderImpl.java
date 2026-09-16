package com.spotify.spotify_backend.infrastructure.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;
import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.spotify.spotify_backend.application.usecase.JwtProvider;
import com.spotify.spotify_backend.domain.model.Role;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtProviderImpl implements JwtProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expirationTime;

    @Value("${jwt.refresh-expiration:604800000}")
    private long refreshExpirationTime;

    @PostConstruct
    void validateConfiguration() {
        getKey();
        if (expirationTime <= 0 || refreshExpirationTime <= 0) {
            throw new IllegalStateException("JWT expiration values must be positive");
        }
    }

    private SecretKey getKey() {

        return Keys.hmacShaKeyFor(
                secretKey.getBytes(StandardCharsets.UTF_8)
        );
    }

    @Override
    public String generateToken(
            Long userId,
            Role role
    ) {

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("role", role.name())
                .claim("tokenType", "access")
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(
                                System.currentTimeMillis()
                                        + expirationTime
                        )
                )
                .signWith(getKey())
                .compact();
    }

    @Override
    public String generateRefreshToken(Long userId) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("tokenType", "refresh")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpirationTime))
                .signWith(getKey())
                .compact();
    }

    @Override
    public boolean validateToken(
            String token
    ) {

        try {

            Claims claims = extractClaims(token);
            return "access".equals(claims.get("tokenType", String.class));

        } catch (Exception e) {

            return false;
        }
    }

    @Override
    public boolean validateRefreshToken(String token) {
        try {
            Claims claims = extractClaims(token);
            return "refresh".equals(claims.get("tokenType", String.class));
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Long extractUserId(
            String token
    ) {

        Claims claims = extractClaims(token);

        return Long.valueOf(
                claims.getSubject()
        );
    }

    @Override
    public Role extractRole(
            String token
    ) {

        Claims claims = extractClaims(token);

        String role = claims.get("role", String.class);
        if (role == null) {
            throw new IllegalArgumentException("Missing role claim");
        }
        return Role.valueOf(role);
    }

    private Claims extractClaims(
            String token
    ) {

        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
