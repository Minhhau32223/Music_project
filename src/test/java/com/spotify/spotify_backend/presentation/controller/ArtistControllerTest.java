package com.spotify.spotify_backend.presentation.controller;

import com.spotify.spotify_backend.BaseIntegrationTest;
import com.spotify.spotify_backend.application.dto.Request.CreateArtistRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests cho ArtistController.
 * - GET endpoints: PUBLIC.
 * - POST, PUT, DELETE: ADMIN only.
 */
@DisplayName("ArtistController Tests")
class ArtistControllerTest extends BaseIntegrationTest {

    private static final String BASE_URL = "/api/artists";

    private String userToken;

    @BeforeEach
    void setUp() throws Exception {
        userToken = registerAndLoginDefaultUser();
    }

    // ===================== GET ALL ARTISTS =====================

    @Nested
    @DisplayName("GET /api/artists/")
    class GetAllArtistsTests {

        @Test
        @DisplayName("TC-ARTIST-01: Lấy tất cả nghệ sĩ - public")
        void getAllArtists_public_shouldReturn200() throws Exception {
            getRequest(BASE_URL + "/", null)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray());
        }
    }

    // ===================== GET BY ID =====================

    @Nested
    @DisplayName("GET /api/artists/{id}/")
    class GetArtistByIdTests {

        @Test
        @DisplayName("TC-ARTIST-02: Lấy nghệ sĩ không tồn tại - 404")
        void getArtistById_notFound_shouldReturn404() throws Exception {
            getRequest(BASE_URL + "/99999/", null)
                    .andExpect(status().isNotFound());
        }
    }

    // ===================== CREATE ARTIST (ADMIN) =====================

    @Nested
    @DisplayName("POST /api/artists/create/ (ADMIN only)")
    class CreateArtistTests {

        @Test
        @DisplayName("TC-ARTIST-03: Tạo nghệ sĩ với USER token - 403 Forbidden")
        void createArtist_withUserToken_shouldReturn403() throws Exception {
            var request = new CreateArtistRequest("Test Artist", "Bio text", "http://image.url");
            postRequest(BASE_URL + "/create/", request, userToken)
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("TC-ARTIST-04: Tạo nghệ sĩ không có JWT - 401 Unauthorized")
        void createArtist_withoutToken_shouldReturn401() throws Exception {
            var request = new CreateArtistRequest("Test Artist", "Bio text", "http://image.url");
            postRequest(BASE_URL + "/create/", request, null)
                    .andExpect(status().isUnauthorized());
        }
    }

    // ===================== UPDATE ARTIST (ADMIN) =====================

    @Nested
    @DisplayName("PUT /api/artists/{id}/ (ADMIN only)")
    class UpdateArtistTests {

        @Test
        @DisplayName("TC-ARTIST-05: Cập nhật nghệ sĩ với USER token - 403 Forbidden")
        void updateArtist_withUserToken_shouldReturn403() throws Exception {
            putRequest(BASE_URL + "/1/", "{\"name\":\"Updated Artist\"}", userToken)
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("TC-ARTIST-06: Cập nhật nghệ sĩ không có JWT - 401 Unauthorized")
        void updateArtist_withoutToken_shouldReturn401() throws Exception {
            putRequest(BASE_URL + "/1/", "{\"name\":\"Updated Artist\"}", null)
                    .andExpect(status().isUnauthorized());
        }
    }

    // ===================== DELETE ARTIST (ADMIN) =====================

    @Nested
    @DisplayName("DELETE /api/artists/{id}/ (ADMIN only)")
    class DeleteArtistTests {

        @Test
        @DisplayName("TC-ARTIST-07: Xóa nghệ sĩ với USER token - 403 Forbidden")
        void deleteArtist_withUserToken_shouldReturn403() throws Exception {
            deleteRequest(BASE_URL + "/1/", userToken)
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("TC-ARTIST-08: Xóa nghệ sĩ không có JWT - 401 Unauthorized")
        void deleteArtist_withoutToken_shouldReturn401() throws Exception {
            deleteRequest(BASE_URL + "/1/", null)
                    .andExpect(status().isUnauthorized());
        }
    }
}
