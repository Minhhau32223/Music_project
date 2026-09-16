package com.spotify.spotify_backend.presentation.controller;

import com.spotify.spotify_backend.BaseIntegrationTest;
import com.spotify.spotify_backend.application.usecase.JwtProvider;
import com.spotify.spotify_backend.domain.model.Role;
import com.spotify.spotify_backend.infrastructure.persistence.entity.ArtistEntity;
import com.spotify.spotify_backend.infrastructure.persistence.repository.ArtistJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ArtistContentSecurityIntegrationTest extends BaseIntegrationTest {
    @Autowired JwtProvider jwt;
    @Autowired ArtistJpaRepository artists;

    @Test
    void creatorRoutesRequireArtistRoleAndResolveProfileFromJwt() throws Exception {
        getRequest("/api/artist/me", null).andExpect(status().isUnauthorized());
        getRequest("/api/artist/me", jwt.generateToken(10L, Role.USER)).andExpect(status().isForbidden());

        artists.saveAndFlush(ArtistEntity.builder().userId(11L).name("owner").bio("bio").profileImage("image").build());
        getRequest("/api/artist/me", jwt.generateToken(11L, Role.ARTIST))
                .andExpect(status().isOk());
    }

    @Test
    void moderationQueueIsAdminOnly() throws Exception {
        getRequest("/api/admin/songs/pending", jwt.generateToken(11L, Role.ARTIST))
                .andExpect(status().isForbidden());
        getRequest("/api/admin/songs/pending", jwt.generateToken(12L, Role.ADMIN))
                .andExpect(status().isOk());
    }
}
