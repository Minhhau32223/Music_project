package com.spotify.spotify_backend.presentation.controller;

import com.spotify.spotify_backend.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests cho SongController.
 * - GET endpoints: PUBLIC (không cần JWT).
 * - POST, DELETE: ADMIN only.
 * 
 * Lưu ý: CreateSong dùng MULTIPART_FORM_DATA (@ModelAttribute).
 * Test tập trung vào security rules và public read endpoints.
 * Upload thực tế tới Cloudinary bị skip (cloudinary config = "test").
 */
@DisplayName("SongController Tests")
class SongControllerTest extends BaseIntegrationTest {

    private static final String BASE_URL = "/api/songs";
    private static final String FILTER_URL = "/api/songs/filter";

    private String userToken;

    @BeforeEach
    void setUp() throws Exception {
        userToken = registerAndLoginDefaultUser();
    }

    // ===================== GET ALL SONGS =====================

    @Nested
    @DisplayName("GET /api/songs/")
    class GetAllSongsTests {

        @Test
        @DisplayName("TC-SONG-01: Lấy tất cả bài hát - public, không cần JWT")
        void getAllSongs_public_shouldReturn200() throws Exception {
            getRequest(BASE_URL + "/", null)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray());
        }

        @Test
        @DisplayName("TC-SONG-02: Lấy tất cả bài hát - có JWT vẫn OK")
        void getAllSongs_withToken_shouldReturn200() throws Exception {
            getRequest(BASE_URL + "/", userToken)
                    .andExpect(status().isOk());
        }
    }

    // ===================== GET BY ID =====================

    @Nested
    @DisplayName("GET /api/songs/{id}/")
    class GetSongByIdTests {

        @Test
        @DisplayName("TC-SONG-03: Lấy bài hát không tồn tại - 404")
        void getSongById_notFound_shouldReturn404() throws Exception {
            getRequest(BASE_URL + "/99999/", null)
                    .andExpect(status().isNotFound());
        }
    }

    // ===================== FILTER SONGS =====================

    @Nested
    @DisplayName("GET /api/songs/filter")
    class FilterSongsTests {

        @Test
        @DisplayName("TC-SONG-04: Filter bài hát không có params - trả về tất cả")
        void filterSongs_noParams_shouldReturn200() throws Exception {
            getRequest(FILTER_URL, null)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray());
        }

        @Test
        @DisplayName("TC-SONG-05: Filter bài hát với sortBy hợp lệ")
        void filterSongs_validSortBy_shouldReturn200() throws Exception {
            mockMvc.perform(
                    org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                            .get(FILTER_URL)
                            .param("sortBy", "playCount")
                            .param("sortDirection", "DESC")
                            .param("page", "0")
                            .param("size", "5")
            ).andExpect(status().isOk());
        }

        @Test
        @DisplayName("TC-SONG-06: Filter bài hát với sortBy không hợp lệ - 400")
        void filterSongs_invalidSortBy_shouldReturn400() throws Exception {
            mockMvc.perform(
                    org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                            .get(FILTER_URL)
                            .param("sortBy", "maliciousField; DROP TABLE songs")
            ).andExpect(status().isBadRequest());
        }
    }

    // ===================== CREATE SONG (ADMIN) =====================

    @Nested
    @DisplayName("POST /api/songs/create/ (ADMIN only)")
    class CreateSongTests {

        @Test
        @DisplayName("TC-SONG-07: Tạo bài hát với USER token - 403 Forbidden")
        void createSong_withUserToken_shouldReturn403() throws Exception {
            MockMultipartFile mp3 = new MockMultipartFile("file", "song.mp3", "audio/mpeg", "fake-mp3".getBytes());
            MockMultipartFile img = new MockMultipartFile("image", "cover.jpg", "image/jpeg", "fake-img".getBytes());

            multipartRequest(BASE_URL + "/create/", userToken, mp3, img)
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("TC-SONG-08: Tạo bài hát không có JWT - 401 Unauthorized")
        void createSong_withoutToken_shouldReturn401() throws Exception {
            MockMultipartFile mp3 = new MockMultipartFile("file", "song.mp3", "audio/mpeg", "fake-mp3".getBytes());
            MockMultipartFile img = new MockMultipartFile("image", "cover.jpg", "image/jpeg", "fake-img".getBytes());

            multipartRequest(BASE_URL + "/create/", null, mp3, img)
                    .andExpect(status().isUnauthorized());
        }
    }

    // ===================== DELETE SONG (ADMIN) =====================

    @Nested
    @DisplayName("DELETE /api/songs/{id}/ (ADMIN only)")
    class DeleteSongTests {

        @Test
        @DisplayName("TC-SONG-09: Xóa bài hát với USER token - 403 Forbidden")
        void deleteSong_withUserToken_shouldReturn403() throws Exception {
            deleteRequest(BASE_URL + "/1/", userToken)
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("TC-SONG-10: Xóa bài hát không có JWT - 401 Unauthorized")
        void deleteSong_withoutToken_shouldReturn401() throws Exception {
            deleteRequest(BASE_URL + "/1/", null)
                    .andExpect(status().isUnauthorized());
        }
    }
}
