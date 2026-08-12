package com.spotify.spotify_backend.presentation.controller;

import com.spotify.spotify_backend.BaseIntegrationTest;
import com.spotify.spotify_backend.application.dto.Request.CreatePlaylistRequest;
import com.spotify.spotify_backend.application.dto.Request.UpdatePlaylistRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests cho PlaylistController + PlaylistSongController.
 * - GET: PUBLIC
 * - POST create, PUT, DELETE: AUTHENTICATED (USER)
 * - POST songs, DELETE songs: AUTHENTICATED
 */
@DisplayName("PlaylistController Tests")
class PlaylistControllerTest extends BaseIntegrationTest {

    private static final String BASE_URL = "/api/playlists";

    private String userToken;
    private Long createdPlaylistId;

    @BeforeEach
    void setUp() throws Exception {
        userToken = registerAndLoginDefaultUser();

        // Get current userId
        MvcResult meResult = getRequest("/api/users/me/", userToken).andReturn();
        Long currentUserId = objectMapper.readTree(meResult.getResponse().getContentAsString()).get("id").asLong();

        // Tạo một playlist để dùng trong các test
        var createRequest = new CreatePlaylistRequest("My Test Playlist", null, currentUserId);
        MvcResult result = postRequest(BASE_URL + "/create", createRequest, userToken)
                .andExpect(status().isOk())
                .andReturn();

        String body = result.getResponse().getContentAsString();
        createdPlaylistId = objectMapper.readTree(body).get("id").asLong();
    }

    // ===================== GET ALL PLAYLISTS =====================

    @Nested
    @DisplayName("GET /api/playlists/")
    class GetAllPlaylistsTests {

        @Test
        @DisplayName("TC-PLAYLIST-01: Lấy tất cả playlist - public")
        void getAllPlaylists_public_shouldReturn200() throws Exception {
            getRequest(BASE_URL + "/", null)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
        }
    }

    // ===================== GET BY ID =====================

    @Nested
    @DisplayName("GET /api/playlists/{id}")
    class GetPlaylistByIdTests {

        @Test
        @DisplayName("TC-PLAYLIST-02: Lấy playlist đã tạo - thành công")
        void getPlaylistById_exists_shouldReturn200() throws Exception {
            getRequest(BASE_URL + "/" + createdPlaylistId, null)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(createdPlaylistId))
                    .andExpect(jsonPath("$.name").value("My Test Playlist"));
        }

        @Test
        @DisplayName("TC-PLAYLIST-03: Lấy playlist không tồn tại - 404")
        void getPlaylistById_notFound_shouldReturn404() throws Exception {
            getRequest(BASE_URL + "/99999", null)
                    .andExpect(status().isNotFound());
        }
    }

    // ===================== CREATE PLAYLIST =====================

    @Nested
    @DisplayName("POST /api/playlists/create (AUTHENTICATED)")
    class CreatePlaylistTests {

        @Test
        @DisplayName("TC-PLAYLIST-04: Tạo playlist thành công")
        void createPlaylist_withValidToken_shouldReturn200() throws Exception {
            var request = new CreatePlaylistRequest("New Playlist", null, null);
            postRequest(BASE_URL + "/create", request, userToken)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("New Playlist"));
        }

        @Test
        @DisplayName("TC-PLAYLIST-05: Tạo playlist không có JWT - 401")
        void createPlaylist_withoutToken_shouldReturn401() throws Exception {
            var request = new CreatePlaylistRequest("New Playlist", null, null);
            postRequest(BASE_URL + "/create", request, null)
                    .andExpect(status().isUnauthorized());
        }
    }

    // ===================== UPDATE PLAYLIST =====================

    @Nested
    @DisplayName("PUT /api/playlists/{id} (AUTHENTICATED)")
    class UpdatePlaylistTests {

        @Test
        @DisplayName("TC-PLAYLIST-06: Cập nhật playlist thành công - name only (multipart)")
        void updatePlaylist_withValidToken_shouldReturn200() throws Exception {
            // UpdatePlaylistRequest dùng @ModelAttribute nên dùng multipart builder (PUT)
            // Trường String truyền bằng .param(), trường file truyền bằng .file()
            mockMvc.perform(
                    org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                            .multipart(org.springframework.http.HttpMethod.PUT, BASE_URL + "/" + createdPlaylistId)
                            .param("name", "Updated Playlist Name")
                            .header("Authorization", "Bearer " + userToken)
            ).andExpect(status().isOk())
             .andExpect(jsonPath("$.name").value("Updated Playlist Name"));
        }

        @Test
        @DisplayName("TC-PLAYLIST-07: Cập nhật playlist không có JWT - 401")
        void updatePlaylist_withoutToken_shouldReturn401() throws Exception {
            var request = new UpdatePlaylistRequest("Updated Name", null);
            putRequest(BASE_URL + "/" + createdPlaylistId, request, null)
                    .andExpect(status().isUnauthorized());
        }
    }

    // ===================== DELETE PLAYLIST =====================

    @Nested
    @DisplayName("DELETE /api/playlists/{id} (AUTHENTICATED)")
    class DeletePlaylistTests {

        @Test
        @DisplayName("TC-PLAYLIST-08: Xóa playlist thành công")
        void deletePlaylist_withValidToken_shouldReturn200OrNoContent() throws Exception {
            deleteRequest(BASE_URL + "/" + createdPlaylistId, userToken)
                    .andExpect(status().is2xxSuccessful());
        }

        @Test
        @DisplayName("TC-PLAYLIST-09: Xóa playlist không có JWT - 401")
        void deletePlaylist_withoutToken_shouldReturn401() throws Exception {
            deleteRequest(BASE_URL + "/" + createdPlaylistId, null)
                    .andExpect(status().isUnauthorized());
        }
    }

    // ===================== PLAYLIST SONGS =====================

    @Nested
    @DisplayName("PlaylistSongController Tests")
    class PlaylistSongTests {

        @Test
        @DisplayName("TC-PLAYLIST-10: Lấy danh sách bài hát trong playlist - public")
        void getPlaylistSongs_public_shouldReturn200() throws Exception {
            getRequest(BASE_URL + "/" + createdPlaylistId + "/songs", null)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray());
        }

        @Test
        @DisplayName("TC-PLAYLIST-11: Thêm bài hát vào playlist - không có JWT thì 401")
        void addSongToPlaylist_withoutToken_shouldReturn401() throws Exception {
            var request = new com.spotify.spotify_backend.application.dto.Request.AddSongToPlaylistRequest(1L);
            postRequest(BASE_URL + "/" + createdPlaylistId + "/songs", request, null)
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("TC-PLAYLIST-12: Thêm bài hát không tồn tại - 404")
        void addSongToPlaylist_songNotFound_shouldReturn404() throws Exception {
            var request = new com.spotify.spotify_backend.application.dto.Request.AddSongToPlaylistRequest(99999L);
            postRequest(BASE_URL + "/" + createdPlaylistId + "/songs", request, userToken)
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("TC-PLAYLIST-13: Xóa bài hát khỏi playlist - không có JWT thì 401")
        void removeSongFromPlaylist_withoutToken_shouldReturn401() throws Exception {
            deleteRequest(BASE_URL + "/" + createdPlaylistId + "/songs/1", null)
                    .andExpect(status().isUnauthorized());
        }
    }
}
