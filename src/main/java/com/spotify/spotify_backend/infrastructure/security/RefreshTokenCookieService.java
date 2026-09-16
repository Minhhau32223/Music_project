package com.spotify.spotify_backend.infrastructure.security;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenCookieService {
    public static final String COOKIE_NAME = "nocturne_refresh";

    @Value("${auth.refresh-cookie.secure:false}")
    private boolean secure;

    @Value("${auth.refresh-cookie.same-site:Lax}")
    private String sameSite;

    @Value("${jwt.refresh-expiration:604800000}")
    private long refreshExpirationTime;

    public ResponseCookie create(String token) {
        return baseCookie(token)
                .maxAge(Duration.ofMillis(refreshExpirationTime))
                .build();
    }

    public ResponseCookie clear() {
        return baseCookie("")
                .maxAge(Duration.ZERO)
                .build();
    }

    private ResponseCookie.ResponseCookieBuilder baseCookie(String value) {
        return ResponseCookie.from(COOKIE_NAME, value)
                .httpOnly(true)
                .secure(secure)
                .sameSite(sameSite)
                .path("/api/auth");
    }
}
