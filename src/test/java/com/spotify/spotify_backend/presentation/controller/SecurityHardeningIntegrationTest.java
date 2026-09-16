package com.spotify.spotify_backend.presentation.controller;

import com.spotify.spotify_backend.BaseIntegrationTest;
import com.spotify.spotify_backend.application.usecase.JwtProvider;
import com.spotify.spotify_backend.domain.model.Role;
import com.spotify.spotify_backend.infrastructure.persistence.entity.PlaylistEntity;
import com.spotify.spotify_backend.infrastructure.persistence.repository.PlaylistJpaRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class SecurityHardeningIntegrationTest extends BaseIntegrationTest {
    @Autowired JwtProvider jwt;
    @Autowired PlaylistJpaRepository playlists;
    @Value("${jwt.secret}") String secret;

    @ParameterizedTest
    @ValueSource(strings = {"/api/favorites/1", "/api/artist-follows/1", "/api/playlists/create"})
    void personalMutationsRequireJwt(String path) throws Exception {
        postRequest(path, Map.of(), null).andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401)).andExpect(jsonPath("$.path").value(path))
                .andExpect(jsonPath("$.timestamp").isString());
    }

    @Test
    void createsPlaylistForJwtUserRegardlessOfInjectedBodyUserId() throws Exception {
        postRequest("/api/playlists/create", Map.of("name", "mine", "userId", 999L),
                jwt.generateToken(101L, Role.USER))
                .andExpect(status().isOk()).andExpect(jsonPath("$.userId").value(101));
    }

    @Test
    void nonOwnerCannotChangePlaylistOrItsSongs() throws Exception {
        var playlist = playlists.saveAndFlush(PlaylistEntity.builder().name("owner").userId(101L).build());
        String path = "/api/playlists/" + playlist.getId();
        String token = jwt.generateToken(102L, Role.USER);
        mockMvc.perform(multipart(HttpMethod.PUT, path).param("name", "stolen")
                .header("Authorization", "Bearer " + token)).andExpect(status().isForbidden());
        deleteRequest(path, token).andExpect(status().isForbidden());
        postRequest(path + "/songs", Map.of("songId", 1), token).andExpect(status().isForbidden());
        deleteRequest(path + "/songs/1", token).andExpect(status().isForbidden());
        getRequest(path, null).andExpect(status().isOk()).andExpect(jsonPath("$.name").value("owner"));
    }

    @Test
    void userAndAdminRolesHaveDistinctCatalogPermissions() throws Exception {
        deleteRequest("/api/songs/999999/", jwt.generateToken(101L, Role.USER))
                .andExpect(status().isForbidden()).andExpect(jsonPath("$.timestamp").isString());
        deleteRequest("/api/songs/999999/", jwt.generateToken(102L, Role.ADMIN))
                .andExpect(status().isNotFound());
    }

    @Test
    void malformedExpiredAndMissingRoleTokensAlwaysReturnJson401() throws Exception {
        var key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        String expired = Jwts.builder().subject("101").claim("role", "USER")
                .expiration(new Date(0)).signWith(key).compact();
        String noRole = Jwts.builder().subject("101").signWith(key).compact();
        for (String token : new String[]{"invalid", expired, noRole}) {
            getRequest("/api/recommendations/top-songs", token).andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.status").value(401)).andExpect(jsonPath("$.timestamp").isString());
        }
    }

    @Test
    void validationAndTypeErrorsUse400AndFieldMessages() throws Exception {
        postRequest("/api/auth/register/", Map.of("username", "", "email", "bad", "password", ""),
                null).andExpect(status().isBadRequest()).andExpect(jsonPath("$.errors.email").exists());
        getRequest("/api/songs/not-a-number/", null).andExpect(status().isBadRequest());
    }

    @Test
    void corsAcceptsConfiguredFrontendAndRejectsUnknownOrigin() throws Exception {
        mockMvc.perform(options("/api/favorites/1").header("Origin", "http://localhost:5173")
                .header("Access-Control-Request-Method", "POST")
                .header("Access-Control-Request-Headers", "Authorization,Content-Type"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"));
        mockMvc.perform(options("/api/favorites/1").header("Origin", "https://untrusted.example")
                .header("Access-Control-Request-Method", "POST")).andExpect(status().isForbidden());
    }

    @Test
    void canonicalFollowRoutesAndHealthAndDocumentationAreAvailable() throws Exception {
        String token = jwt.generateToken(101L, Role.USER);
        getRequest("/api/artist-follows/me", token).andExpect(status().isOk());
        getRequest("/actuator/health", null).andExpect(status().isOk()).andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.components").doesNotExist());
        getRequest("/actuator/env", null).andExpect(status().isUnauthorized());
        getRequest("/v3/api-docs", null).andExpect(status().isOk())
                .andExpect(jsonPath("$.components.securitySchemes.bearerAuth.scheme").value("bearer"))
                .andExpect(jsonPath("$.paths['/api/recommendations/personalized'].get.security[0].bearerAuth").exists());
    }
}

