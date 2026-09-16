package com.spotify.spotify_backend.presentation.controller;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.spotify.spotify_backend.BaseIntegrationTest;
import com.spotify.spotify_backend.infrastructure.persistence.entity.AlbumEntity;
import com.spotify.spotify_backend.infrastructure.persistence.entity.ArtistEntity;
import com.spotify.spotify_backend.infrastructure.persistence.entity.SongEntity;
import com.spotify.spotify_backend.infrastructure.persistence.repository.AlbumJpaRepository;
import com.spotify.spotify_backend.infrastructure.persistence.repository.ArtistJpaRepository;
import com.spotify.spotify_backend.infrastructure.persistence.repository.SongJpaRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

class PhaseSevenApiAlignmentIntegrationTest extends BaseIntegrationTest {

    @Autowired SongJpaRepository songs;
    @Autowired AlbumJpaRepository albums;
    @Autowired ArtistJpaRepository artists;
    @Autowired JdbcTemplate jdbc;

    private SongEntity song(String name, long plays, long artistId, long albumId) {
        return songs.saveAndFlush(SongEntity.builder()
                .name(name)
                .playCount(plays)
                .artistId(artistId)
                .albumId(albumId)
                .file("https://example.test/" + name + ".mp3")
                .image("https://example.test/cover.jpg")
                .lyrics("lyrics")
                .duration("180")
                .createdAt(LocalDateTime.now())
                .build());
    }

    @Test
    void searchUsesAuditedShapeCorrectSongMappingAndPublishedOnlyCatalog() throws Exception {
        var artist = artists.save(ArtistEntity.builder()
                .name("Linh Nocturne")
                .bio("Vietnamese electronic artist")
                .profileImage("image")
                .build());
        var album = albums.saveAndFlush(AlbumEntity.builder()
                .name("Thành Phố Mưa")
                .image("image")
                .description("description")
                .bgColor("#111111")
                .build());
        var published = song("Đêm Không Ngủ", 8, artist.getId(), album.getId());
        var draft = song("Đêm Bản Nháp", 999, artist.getId(), album.getId());
        jdbc.update("UPDATE songs SET status = 'DRAFT' WHERE id = ?", draft.getId());

        mockMvc.perform(get("/api/search").param("keyword", "  đÊm  "))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.songs", hasSize(1)))
                .andExpect(jsonPath("$.songs[0].id").value(published.getId()))
                .andExpect(jsonPath("$.songs[0].albumName").value("Thành Phố Mưa"))
                .andExpect(jsonPath("$.songs[0].artistName").value("Linh Nocturne"))
                .andExpect(jsonPath("$.albums").isArray())
                .andExpect(jsonPath("$.artists").isArray())
                .andExpect(jsonPath("$.playlists").doesNotExist());
    }

    @Test
    void searchExcludesDraftAlbumsAndSupportsUnicodeAndSpecialCharacters() throws Exception {
        var visible = albums.saveAndFlush(AlbumEntity.builder()
                .name("Mưa & Neon")
                .image("image")
                .description("description")
                .bgColor("#111111")
                .build());
        var hidden = albums.saveAndFlush(AlbumEntity.builder()
                .name("Mưa & Neon (draft)")
                .image("image")
                .description("description")
                .bgColor("#222222")
                .build());
        jdbc.update("UPDATE albums SET status = 'DRAFT' WHERE id = ?", hidden.getId());

        mockMvc.perform(get("/api/search").param("keyword", "mƯa &"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.albums", hasSize(1)))
                .andExpect(jsonPath("$.albums[0].id").value(visible.getId()));
    }

    @Test
    void filterAndEveryRecommendationSurfaceExcludeNonPublishedSongs() throws Exception {
        var artist = artists.save(ArtistEntity.builder().name("Phase Seven Artist")
                .bio("bio").profileImage("image").build());
        var album = albums.saveAndFlush(AlbumEntity.builder().name("Phase Seven Album")
                .image("image").description("description").bgColor("#111111").build());
        var published = song("Published Signal", 10, artist.getId(), album.getId());
        var draft = song("Draft Signal", 10_000, artist.getId(), album.getId());
        jdbc.update("UPDATE songs SET status = 'DRAFT' WHERE id = ?", draft.getId());

        mockMvc.perform(get("/api/songs/filter")
                        .param("artistId", artist.getId().toString())
                        .param("size", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].id", hasItem(published.getId().intValue())))
                .andExpect(jsonPath("$.content[*].id", not(hasItem(draft.getId().intValue()))));

        mockMvc.perform(get("/api/recommendations/top-songs").param("limit", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id", hasItem(published.getId().intValue())))
                .andExpect(jsonPath("$[*].id", not(hasItem(draft.getId().intValue()))));

        mockMvc.perform(get("/api/recommendations/new-releases").param("limit", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id", hasItem(published.getId().intValue())))
                .andExpect(jsonPath("$[*].id", not(hasItem(draft.getId().intValue()))));
    }
}
