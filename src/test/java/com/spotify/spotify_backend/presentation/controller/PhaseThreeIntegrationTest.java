package com.spotify.spotify_backend.presentation.controller;

import com.spotify.spotify_backend.BaseIntegrationTest;
import com.spotify.spotify_backend.application.usecase.JwtProvider;
import com.spotify.spotify_backend.domain.model.Role;
import com.spotify.spotify_backend.infrastructure.persistence.entity.*;
import com.spotify.spotify_backend.infrastructure.persistence.repository.*;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class PhaseThreeIntegrationTest extends BaseIntegrationTest {
    @Autowired SongJpaRepository songs;
    @Autowired ArtistJpaRepository artists;
    @Autowired AlbumJpaRepository albums;
    @Autowired RecentlyPlayedJpaRepository history;
    @Autowired JwtProvider jwt;

    private SongEntity song(String name, long plays, long artistId, long albumId, LocalDateTime created) {
        return songs.saveAndFlush(SongEntity.builder().name(name).playCount(plays)
                .artistId(artistId).albumId(albumId).file("https://example.test/song.mp3")
                .image("https://example.test/cover.jpg").lyrics("lyrics").duration("180")
                .createdAt(created).build());
    }

    @ParameterizedTest
    @CsvSource({"page,-1", "size,0", "size,101", "sortBy,unknown", "sortDirection,DOWN",
            "sortDirection,desc", "page,2147483647", "size,abc"})
    void invalidFiltersReturn400(String parameter, String value) throws Exception {
        mockMvc.perform(get("/api/songs/filter").param(parameter, value))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400));
    }

    @ParameterizedTest
    @CsvSource({"top-songs,0", "top-songs,101", "new-releases,-1", "new-releases,1000", "top-songs,abc",
            "personalized,0", "personalized,101"})
    void invalidRecommendationLimitsReturn400(String endpoint, String limit) throws Exception {
        mockMvc.perform(get("/api/recommendations/" + endpoint).param("limit", limit)
                .header("Authorization", "Bearer " + jwt.generateToken(9001L, Role.USER)))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void filterCombinesArtistAlbumSortAndPageAndMapsNamesCorrectly() throws Exception {
        var artist = artists.save(ArtistEntity.builder().name("Artist A").bio("bio").profileImage("image").build());
        var album = albums.save(AlbumEntity.builder().name("Album A").image("image").description("desc").bgColor("black").build());
        var now = LocalDateTime.now();
        var first = song("A", 30, artist.getId(), album.getId(), now);
        var second = song("B", 20, artist.getId(), album.getId(), now);
        song("Other album", 999, artist.getId(), album.getId() + 1000, now);
        song("Other artist", 999, artist.getId() + 1000, album.getId(), now);

        mockMvc.perform(get("/api/songs/filter").param("artistId", artist.getId().toString())
                .param("albumId", album.getId().toString()).param("sortBy", "playCount")
                .param("sortDirection", "DESC").param("page", "1").param("size", "1"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id").value(second.getId()))
                .andExpect(jsonPath("$.content[0].albumName").value("Album A"))
                .andExpect(jsonPath("$.content[0].artistName").value("Artist A"))
                .andExpect(jsonPath("$.pageNumber").value(1)).andExpect(jsonPath("$.pageSize").value(1))
                .andExpect(jsonPath("$.totalElements").value(2)).andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.last").value(true));
    }

    @Test
    void chartsAndReleasesSortByActualValuesNotIdentity() throws Exception {
        var newest = song("newest", 10, 1, 1, LocalDateTime.now().plusDays(1));
        var popular = song("popular", 100, 1, 1, LocalDateTime.now().minusDays(1));
        getRequest("/api/recommendations/new-releases?limit=1", null)
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value(newest.getId()));
        getRequest("/api/recommendations/top-songs?limit=1", null)
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value(popular.getId()));
    }

    @Test
    void personalizedUsesJwtHistoryAndExcludesEveryRecentlyPlayedSong() throws Exception {
        var now = LocalDateTime.now();
        var played = song("played", 100, 10, 10, now);
        var candidate = song("preferred artist", 5, 10, 20, now);
        song("unrelated popular", 1000, 30, 30, now);
        history.save(RecentlyPlayedEntity.builder().userId(901L).songId(played.getId()).playedAt(now).build());
        history.save(RecentlyPlayedEntity.builder().userId(901L).songId(played.getId()).playedAt(now.minusMinutes(1)).build());
        String token = jwt.generateToken(901L, Role.USER);
        getRequest("/api/recommendations/personalized?limit=1&userId=999", token)
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value(candidate.getId()));
        getRequest("/api/recommendations/personalized", null).andExpect(status().isUnauthorized());
    }

    @Test
    void fallbackCanFindUnheardSongsBeyondFirstTwoPagesOfTopSongs() throws Exception {
        var now = LocalDateTime.now();
        for (int i = 0; i < 8; i++) {
            var played = song("played " + i, 100 + i, 10, 10, now);
            history.save(RecentlyPlayedEntity.builder().userId(902L).songId(played.getId()).playedAt(now).build());
        }
        var unheard = song("unheard", 1, 20, 20, now);
        getRequest("/api/recommendations/personalized?limit=1", jwt.generateToken(902L, Role.USER))
                .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(unheard.getId()));
        getRequest("/api/recommendations/personalized?limit=1", jwt.generateToken(903L, Role.USER))
                .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].playCount").value(107));
    }
}

