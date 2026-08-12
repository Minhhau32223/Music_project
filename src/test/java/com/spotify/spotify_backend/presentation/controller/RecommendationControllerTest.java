package com.spotify.spotify_backend.presentation.controller;

import com.spotify.spotify_backend.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Integration tests cho RecommendationController.
 * - GET /api/recommendations/top-songs       → PUBLIC
 * - GET /api/recommendations/new-releases    → PUBLIC
 * - GET /api/recommendations/personalized   → JWT required
 */
@DisplayName("RecommendationController Tests")
class RecommendationControllerTest extends BaseIntegrationTest {

    private static final String TOP_SONGS_URL = "/api/recommendations/top-songs";
    private static final String NEW_RELEASES_URL = "/api/recommendations/new-releases";
    private static final String PERSONALIZED_URL = "/api/recommendations/personalized";

    private String userToken;

    @BeforeEach
    void setUp() throws Exception {
        userToken = registerAndLoginDefaultUser();
    }

    // ===================== TOP SONGS =====================

    @Nested
    @DisplayName("GET /api/recommendations/top-songs")
    class TopSongsTests {

        @Test
        @DisplayName("TC-REC-01: Lấy top songs - public, không cần JWT")
        void getTopSongs_public_shouldReturn200() throws Exception {
            getRequest(TOP_SONGS_URL, null)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray());
        }

        @Test
        @DisplayName("TC-REC-02: Lấy top songs với limit custom")
        void getTopSongs_withLimit_shouldReturn200() throws Exception {
            mockMvc.perform(get(TOP_SONGS_URL).param("limit", "5"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray());
        }
    }

    // ===================== NEW RELEASES =====================

    @Nested
    @DisplayName("GET /api/recommendations/new-releases")
    class NewReleasesTests {

        @Test
        @DisplayName("TC-REC-03: Lấy new releases - public, không cần JWT")
        void getNewReleases_public_shouldReturn200() throws Exception {
            getRequest(NEW_RELEASES_URL, null)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray());
        }

        @Test
        @DisplayName("TC-REC-04: Lấy new releases với limit custom")
        void getNewReleases_withLimit_shouldReturn200() throws Exception {
            mockMvc.perform(get(NEW_RELEASES_URL).param("limit", "5"))
                    .andExpect(status().isOk());
        }
    }

    // ===================== PERSONALIZED =====================

    @Nested
    @DisplayName("GET /api/recommendations/personalized")
    class PersonalizedTests {

        @Test
        @DisplayName("TC-REC-05: Lấy gợi ý cá nhân hóa - thành công với JWT")
        void getPersonalized_withToken_shouldReturn200() throws Exception {
            getRequest(PERSONALIZED_URL, userToken)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray());
        }

        @Test
        @DisplayName("TC-REC-06: Lấy gợi ý cá nhân hóa - không có JWT thì 401")
        void getPersonalized_withoutToken_shouldReturn401() throws Exception {
            getRequest(PERSONALIZED_URL, null)
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("TC-REC-07: Lấy gợi ý cá nhân hóa với limit custom")
        void getPersonalized_withLimit_shouldReturn200() throws Exception {
            mockMvc.perform(
                    get(PERSONALIZED_URL)
                            .param("limit", "5")
                            .header("Authorization", "Bearer " + userToken)
            ).andExpect(status().isOk());
        }
    }
}
