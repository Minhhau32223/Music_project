package com.spotify.spotify_backend.presentation.controller;

import com.spotify.spotify_backend.BaseIntegrationTest;
import com.spotify.spotify_backend.application.usecase.JwtProvider;
import com.spotify.spotify_backend.domain.model.Role;
import com.spotify.spotify_backend.domain.repository.CloudinaryRepository;
import com.spotify.spotify_backend.infrastructure.persistence.entity.AlbumEntity;
import com.spotify.spotify_backend.infrastructure.persistence.entity.ArtistEntity;
import com.spotify.spotify_backend.infrastructure.persistence.repository.AlbumJpaRepository;
import com.spotify.spotify_backend.infrastructure.persistence.repository.ArtistJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CatalogMutationIntegrationTest extends BaseIntegrationTest {
    @Autowired JwtProvider jwt;
    @Autowired AlbumJpaRepository albums;
    @Autowired ArtistJpaRepository artists;
    @MockitoBean CloudinaryRepository cloudinary;

    @Test
    void adminCanCreateUpdateAndDeleteSongWithoutCallingExternalStorage() throws Exception {
        var album = albums.save(AlbumEntity.builder().name("album").image("image").description("desc").bgColor("black").build());
        var artist = artists.save(ArtistEntity.builder().name("artist").bio("bio").profileImage("image").build());
        String token = jwt.generateToken(123L, Role.ADMIN);
        when(cloudinary.uploadFile(any(), eq("songs"))).thenReturn("https://example.test/music.mp3");
        when(cloudinary.uploadFile(any(), eq("song-images"))).thenReturn("https://example.test/cover.jpg");
        var result = mockMvc.perform(multipart("/api/songs/create/")
                .file(new MockMultipartFile("file", "song.mp3", "audio/mpeg", new byte[]{1}))
                .file(new MockMultipartFile("image", "cover.jpg", "image/jpeg", new byte[]{1}))
                .param("name", "created").param("lyrics", "lyrics").param("duration", "180")
                .param("playCount", "0").param("albumId", album.getId().toString())
                .param("artistId", artist.getId().toString()).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()).andExpect(jsonPath("$.albumName").value("album"))
                .andExpect(jsonPath("$.artistName").value("artist")).andReturn();
        long id = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
        mockMvc.perform(multipart(HttpMethod.PUT, "/api/songs/" + id)
                .param("name", "updated").param("lyrics", "lyrics").param("duration", "180")
                .param("albumId", album.getId().toString()).param("artistId", artist.getId().toString())
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()).andExpect(jsonPath("$.name").value("updated"));
        getRequest("/api/recommendations/new-releases?limit=1", null)
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].name").value("updated"));
        deleteRequest("/api/songs/" + id + "/", token).andExpect(status().isNoContent());
        getRequest("/api/songs/" + id + "/", null).andExpect(status().isNotFound());
    }
}
