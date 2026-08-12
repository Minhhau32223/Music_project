package com.spotify.spotify_backend.presentation.controller;

import com.spotify.spotify_backend.BaseIntegrationTest;
import com.spotify.spotify_backend.application.dto.Request.CreateAlbumRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests cho AlbumController.
 * - GET endpoints: PUBLIC.
 * - POST, PUT, DELETE: ADMIN only.
 */
@DisplayName("AlbumController Tests")
class AlbumControllerTest extends BaseIntegrationTest {

    private static final String BASE_URL = "/api/albums";

    private String userToken;

    @BeforeEach
    void setUp() throws Exception {
        userToken = registerAndLoginDefaultUser();
    }

    // ===================== GET ALL ALBUMS =====================

    @Nested
    @DisplayName("GET /api/albums/")
    class GetAllAlbumsTests {

        @Test
        @DisplayName("TC-ALBUM-01: Lấy tất cả album - public")
        void getAllAlbums_public_shouldReturn200() throws Exception {
            getRequest(BASE_URL + "/", null)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray());
        }
    }

    // ===================== GET BY ID =====================

    @Nested
    @DisplayName("GET /api/albums/{id}/")
    class GetAlbumByIdTests {

        @Test
        @DisplayName("TC-ALBUM-02: Lấy album không tồn tại - 404")
        void getAlbumById_notFound_shouldReturn404() throws Exception {
            getRequest(BASE_URL + "/99999/", null)
                    .andExpect(status().isNotFound());
        }
    }

    // ===================== GET ALBUM SONGS =====================

    @Nested
    @DisplayName("GET /api/albums/{id}/songs/")
    class GetAlbumSongsTests {

        @Test
        @DisplayName("TC-ALBUM-03: Lấy bài hát của album không tồn tại - 404")
        void getAlbumSongs_notFound_shouldReturn404() throws Exception {
            getRequest(BASE_URL + "/99999/songs/", null)
                    .andExpect(status().isNotFound());
        }
    }

    // ===================== CREATE ALBUM (ADMIN) =====================

    @Nested
    @DisplayName("POST /api/albums/create/ (ADMIN only)")
    class CreateAlbumTests {

        @Test
        @DisplayName("TC-ALBUM-04: Tạo album với USER token - 403 Forbidden")
        void createAlbum_withUserToken_shouldReturn403() throws Exception {
            var request = new CreateAlbumRequest("Test Album", "image-url", "Description", "#FFFFFF");
            postRequest(BASE_URL + "/create/", request, userToken)
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("TC-ALBUM-05: Tạo album không có JWT - 401 Unauthorized")
        void createAlbum_withoutToken_shouldReturn401() throws Exception {
            var request = new CreateAlbumRequest("Test Album", "image-url", "Description", "#FFFFFF");
            postRequest(BASE_URL + "/create/", request, null)
                    .andExpect(status().isUnauthorized());
        }
    }

    // ===================== UPDATE ALBUM (ADMIN) =====================

    @Nested
    @DisplayName("PUT /api/albums/{id}/ (ADMIN only)")
    class UpdateAlbumTests {

        @Test
        @DisplayName("TC-ALBUM-06: Cập nhật album với USER token - 403 Forbidden")
        void updateAlbum_withUserToken_shouldReturn403() throws Exception {
            putRequest(BASE_URL + "/1/", "{\"name\":\"Updated\"}", userToken)
                    .andExpect(status().isForbidden());
        }
    }

    // ===================== DELETE ALBUM (ADMIN) =====================

    @Nested
    @DisplayName("DELETE /api/albums/{id}/ (ADMIN only)")
    class DeleteAlbumTests {

        @Test
        @DisplayName("TC-ALBUM-07: Xóa album với USER token - 403 Forbidden")
        void deleteAlbum_withUserToken_shouldReturn403() throws Exception {
            deleteRequest(BASE_URL + "/1/", userToken)
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("TC-ALBUM-08: Xóa album không có JWT - 401 Unauthorized")
        void deleteAlbum_withoutToken_shouldReturn401() throws Exception {
            deleteRequest(BASE_URL + "/1/", null)
                    .andExpect(status().isUnauthorized());
        }
    }
}
