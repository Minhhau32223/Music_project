package com.spotify.spotify_backend.presentation.controller;

import com.spotify.spotify_backend.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Integration tests cho SearchController.
 * - GET /api/search?keyword=... → PUBLIC
 */
@DisplayName("SearchController Tests")
class SearchControllerTest extends BaseIntegrationTest {

    private static final String SEARCH_URL = "/api/search";

    @Nested
    @DisplayName("GET /api/search")
    class SearchTests {

        @Test
        @DisplayName("TC-SEARCH-01: Tìm kiếm không cần JWT - public")
        void search_withKeyword_shouldReturn200() throws Exception {
            mockMvc.perform(get(SEARCH_URL).param("keyword", "test"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("TC-SEARCH-02: Tìm kiếm với keyword rỗng - vẫn trả về 200")
        void search_withEmptyKeyword_shouldReturn200() throws Exception {
            mockMvc.perform(get(SEARCH_URL).param("keyword", ""))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("TC-SEARCH-03: Tìm kiếm thiếu keyword param - 400")
        void search_withoutKeyword_shouldReturn400() throws Exception {
            mockMvc.perform(get(SEARCH_URL))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("TC-SEARCH-04: Tìm kiếm có JWT vẫn hoạt động")
        void search_withToken_shouldReturn200() throws Exception {
            String token = registerAndLoginDefaultUser();
            getRequest(SEARCH_URL + "?keyword=test", token)
                    .andExpect(status().isOk());
        }
    }
}
