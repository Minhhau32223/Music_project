package com.spotify.spotify_backend.presentation.controller;

import com.spotify.spotify_backend.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests cho StreamingController.
 * - GET /api/stream/song/{id}       → PUBLIC (trả về JSON metadata + Cloudinary URL)
 * - GET /api/stream/song/{id}/play  → PUBLIC (302 Redirect)
 * 
 * Lưu ý: Test với songId không tồn tại để verify 404 behavior.
 * Test với cloudinary giả (cloud_name: test) sẽ không redirect thực tế.
 */
@DisplayName("StreamingController Tests")
class StreamingControllerTest extends BaseIntegrationTest {

    private static final String STREAM_INFO_URL = "/api/stream/song";

    private String userToken;

    @BeforeEach
    void setUp() throws Exception {
        userToken = registerAndLoginDefaultUser();
    }

    // ===================== GET STREAMING INFO =====================

    @Nested
    @DisplayName("GET /api/stream/song/{id}")
    class GetStreamingInfoTests {

        @Test
        @DisplayName("TC-STREAM-01: Lấy streaming info bài hát không tồn tại - 404")
        void getStreamingInfo_songNotFound_shouldReturn404() throws Exception {
            getRequest(STREAM_INFO_URL + "/99999", null)
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("TC-STREAM-02: Lấy streaming info - public, không cần JWT")
        void getStreamingInfo_publicAccess_isAllowed() throws Exception {
            // Endpoint public → không bị 401, nhưng song không tồn tại nên 404
            getRequest(STREAM_INFO_URL + "/99999", null)
                    .andExpect(status().isNotFound());  // 404, bukan 401
        }
    }

    // ===================== PLAY (REDIRECT) =====================

    @Nested
    @DisplayName("GET /api/stream/song/{id}/play")
    class PlayRedirectTests {

        @Test
        @DisplayName("TC-STREAM-03: Play bài hát không tồn tại - 404")
        void playRedirect_songNotFound_shouldReturn404() throws Exception {
            getRequest(STREAM_INFO_URL + "/99999/play", null)
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("TC-STREAM-04: Play endpoint - public, không cần JWT")
        void playRedirect_publicAccess_isAllowed() throws Exception {
            // Endpoint public → không bị 401
            getRequest(STREAM_INFO_URL + "/99999/play", null)
                    .andExpect(status().isNotFound());  // 404, không phải 401
        }
    }
}
