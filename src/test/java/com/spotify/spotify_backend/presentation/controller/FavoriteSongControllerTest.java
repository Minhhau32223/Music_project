package com.spotify.spotify_backend.presentation.controller;

import com.spotify.spotify_backend.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests cho FavoriteSongController.
 * Tất cả endpoint đều yêu cầu JWT Authentication (userId lấy từ token).
 * - POST /api/favorites/{songId}
 * - DELETE /api/favorites/{songId}
 * - GET /api/favorites/me
 */
@DisplayName("FavoriteSongController Tests")
class FavoriteSongControllerTest extends BaseIntegrationTest {

    private static final String BASE_URL = "/api/favorites";

    private String userToken;

    @BeforeEach
    void setUp() throws Exception {
        userToken = registerAndLoginDefaultUser();
    }

    // ===================== GET MY FAVORITES =====================

    @Nested
    @DisplayName("GET /api/favorites/me")
    class GetFavoritesTests {

        @Test
        @DisplayName("TC-FAV-01: Lấy danh sách yêu thích - thành công")
        void getMyFavorites_withToken_shouldReturn200() throws Exception {
            getRequest(BASE_URL + "/me", userToken)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray());
        }

        @Test
        @DisplayName("TC-FAV-02: Lấy danh sách yêu thích - không có JWT thì 401")
        void getMyFavorites_withoutToken_shouldReturn401() throws Exception {
            getRequest(BASE_URL + "/me", null)
                    .andExpect(status().isUnauthorized());
        }
    }

    // ===================== ADD FAVORITE =====================

    @Nested
    @DisplayName("POST /api/favorites/{songId}")
    class AddFavoriteTests {

        @Test
        @DisplayName("TC-FAV-03: Thêm yêu thích - không có JWT thì 401")
        void addFavorite_withoutToken_shouldReturn401() throws Exception {
            postRequest(BASE_URL + "/1", null, null)
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("TC-FAV-04: Thêm yêu thích bài hát không tồn tại - 404")
        void addFavorite_songNotFound_shouldReturn404() throws Exception {
            postRequest(BASE_URL + "/99999", null, userToken)
                    .andExpect(status().isNotFound());
        }
    }

    // ===================== REMOVE FAVORITE =====================

    @Nested
    @DisplayName("DELETE /api/favorites/{songId}")
    class RemoveFavoriteTests {

        @Test
        @DisplayName("TC-FAV-05: Xóa yêu thích - không có JWT thì 401")
        void removeFavorite_withoutToken_shouldReturn401() throws Exception {
            deleteRequest(BASE_URL + "/1", null)
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("TC-FAV-06: Xóa yêu thích bài hát không tồn tại - idempotent, trả về 200/204")
        void removeFavorite_songNotFound_shouldReturn2xx() throws Exception {
            deleteRequest(BASE_URL + "/99999", userToken)
                    .andExpect(status().is2xxSuccessful());
        }
    }
}
