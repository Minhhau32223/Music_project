package com.spotify.spotify_backend.presentation.controller;

import com.spotify.spotify_backend.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests cho ArtistFollowController.
 * Tất cả endpoint đều yêu cầu JWT Authentication (userId lấy từ token).
 * - POST /api/artist-follows/{artistId}/
 * - DELETE /api/artist-follows/{artistId}/
 * - GET /api/artist-follows/me/
 */
@DisplayName("ArtistFollowController Tests")
class ArtistFollowControllerTest extends BaseIntegrationTest {

    private static final String BASE_URL = "/api/artist-follows";

    private String userToken;

    @BeforeEach
    void setUp() throws Exception {
        userToken = registerAndLoginDefaultUser();
    }

    // ===================== GET MY FOLLOWED ARTISTS =====================

    @Nested
    @DisplayName("GET /api/artist-follows/me/")
    class GetFollowedArtistsTests {

        @Test
        @DisplayName("TC-FOLLOW-01: Lấy danh sách nghệ sĩ đang theo dõi - thành công")
        void getFollowedArtists_withToken_shouldReturn200() throws Exception {
            getRequest(BASE_URL + "/me/", userToken)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray());
        }

        @Test
        @DisplayName("TC-FOLLOW-02: Lấy danh sách nghệ sĩ - không có JWT thì 401")
        void getFollowedArtists_withoutToken_shouldReturn401() throws Exception {
            getRequest(BASE_URL + "/me/", null)
                    .andExpect(status().isUnauthorized());
        }
    }

    // ===================== FOLLOW ARTIST =====================

    @Nested
    @DisplayName("POST /api/artist-follows/{artistId}/")
    class FollowArtistTests {

        @Test
        @DisplayName("TC-FOLLOW-03: Follow nghệ sĩ - không có JWT thì 401")
        void followArtist_withoutToken_shouldReturn401() throws Exception {
            postRequest(BASE_URL + "/1/", null, null)
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("TC-FOLLOW-04: Follow nghệ sĩ không tồn tại - 404")
        void followArtist_artistNotFound_shouldReturn404() throws Exception {
            postRequest(BASE_URL + "/99999/", null, userToken)
                    .andExpect(status().isNotFound());
        }
    }

    // ===================== UNFOLLOW ARTIST =====================

    @Nested
    @DisplayName("DELETE /api/artist-follows/{artistId}/")
    class UnfollowArtistTests {

        @Test
        @DisplayName("TC-FOLLOW-05: Unfollow nghệ sĩ - không có JWT thì 401")
        void unfollowArtist_withoutToken_shouldReturn401() throws Exception {
            deleteRequest(BASE_URL + "/1/", null)
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("TC-FOLLOW-06: Unfollow nghệ sĩ không tồn tại - idempotent, trả về 200/204")
        void unfollowArtist_artistNotFound_shouldReturn2xx() throws Exception {
            deleteRequest(BASE_URL + "/99999/", userToken)
                    .andExpect(status().is2xxSuccessful());
        }
    }
}
