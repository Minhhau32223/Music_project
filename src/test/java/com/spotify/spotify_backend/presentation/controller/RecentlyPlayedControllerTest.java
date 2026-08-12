package com.spotify.spotify_backend.presentation.controller;

import com.spotify.spotify_backend.BaseIntegrationTest;
import com.spotify.spotify_backend.application.dto.Request.PlaybackEventRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests cho RecentlyPlayedController.
 * - POST /api/recently-played/play   → JWT required
 * - GET  /api/recently-played/me     → JWT required
 */
@DisplayName("RecentlyPlayedController Tests")
class RecentlyPlayedControllerTest extends BaseIntegrationTest {

    private static final String PLAY_URL = "/api/recently-played/play";
    private static final String HISTORY_URL = "/api/recently-played/me";

    private String userToken;

    @BeforeEach
    void setUp() throws Exception {
        userToken = registerAndLoginDefaultUser();
    }

    // ===================== GET HISTORY =====================

    @Nested
    @DisplayName("GET /api/recently-played/me")
    class GetHistoryTests {

        @Test
        @DisplayName("TC-RECENT-01: Lấy lịch sử nghe nhạc - thành công")
        void getHistory_withToken_shouldReturn200() throws Exception {
            getRequest(HISTORY_URL, userToken)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray());
        }

        @Test
        @DisplayName("TC-RECENT-02: Lấy lịch sử nghe nhạc - không có JWT thì 401")
        void getHistory_withoutToken_shouldReturn401() throws Exception {
            getRequest(HISTORY_URL, null)
                    .andExpect(status().isUnauthorized());
        }
    }

    // ===================== RECORD PLAY =====================

    @Nested
    @DisplayName("POST /api/recently-played/play")
    class RecordPlayTests {

        @Test
        @DisplayName("TC-RECENT-03: Record play - không có JWT thì 401")
        void recordPlay_withoutToken_shouldReturn401() throws Exception {
            var request = new PlaybackEventRequest(1L);
            postRequest(PLAY_URL, request, null)
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("TC-RECENT-04: Record play bài hát không tồn tại - 404")
        void recordPlay_songNotFound_shouldReturn404() throws Exception {
            var request = new PlaybackEventRequest(99999L);
            postRequest(PLAY_URL, request, userToken)
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("TC-RECENT-05: Record play - thiếu songId (null) thì 400")
        void recordPlay_missingSongId_shouldReturn400() throws Exception {
            // Gửi JSON với songId là null → @NotNull violation → 400
            postRequest(PLAY_URL, new PlaybackEventRequest(null), userToken)
                    .andExpect(status().isBadRequest());
        }
    }
}
