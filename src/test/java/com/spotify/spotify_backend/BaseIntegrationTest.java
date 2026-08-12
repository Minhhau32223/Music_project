package com.spotify.spotify_backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Base class cho tất cả Controller Integration Tests.
 * - @SpringBootTest: Load full context.
 * - @AutoConfigureMockMvc: Inject MockMvc.
 * - @ActiveProfiles("test"): Dùng application-test.yml.
 * - @Transactional: Rollback DB sau mỗi test.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public abstract class BaseIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    protected ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    // ======== HTTP HELPER METHODS ========

    protected ResultActions getRequest(String url, String token) throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(url);
        if (token != null && !token.isEmpty()) {
            builder.header("Authorization", "Bearer " + token);
        }
        return mockMvc.perform(builder.contentType(APPLICATION_JSON));
    }

    protected ResultActions postRequest(String url, Object body, String token) throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post(url)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body));
        if (token != null && !token.isEmpty()) {
            builder.header("Authorization", "Bearer " + token);
        }
        return mockMvc.perform(builder);
    }

    protected ResultActions putRequest(String url, Object body, String token) throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put(url)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body));
        if (token != null && !token.isEmpty()) {
            builder.header("Authorization", "Bearer " + token);
        }
        return mockMvc.perform(builder);
    }

    protected ResultActions deleteRequest(String url, String token) throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.delete(url);
        if (token != null && !token.isEmpty()) {
            builder.header("Authorization", "Bearer " + token);
        }
        return mockMvc.perform(builder.contentType(APPLICATION_JSON));
    }

    /**
     * Gửi multipart/form-data POST request (dùng cho Song, Artist, Album có file)
     */
    protected ResultActions multipartRequest(String url, String token, MockMultipartFile... files) throws Exception {
        var builder = MockMvcRequestBuilders.multipart(url);
        for (MockMultipartFile file : files) {
            builder.file(file);
        }
        if (token != null && !token.isEmpty()) {
            builder.header("Authorization", "Bearer " + token);
        }
        return mockMvc.perform(builder);
    }

    /**
     * Extract accessToken từ response của /api/auth/login/
     */
    protected String extractToken(MvcResult result) throws Exception {
        String body = result.getResponse().getContentAsString();
        return objectMapper.readTree(body).get("accessToken").asText();
    }

    protected String toJson(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    // ======== SHARED SETUP HELPERS ========

    /**
     * Đăng ký + Đăng nhập với credentials cụ thể, trả về token.
     */
    protected String registerAndLogin(String username, String email, String password) throws Exception {
        var register = new com.spotify.spotify_backend.application.dto.Request.RegisterRequest(username, email, password);
        postRequest("/api/auth/register/", register, null);

        var login = new com.spotify.spotify_backend.application.dto.Request.LoginRequest(email, password);
        MvcResult result = postRequest("/api/auth/login/", login, null)
                .andExpect(status().isOk())
                .andReturn();
        return extractToken(result);
    }

    /**
     * Đăng ký + Đăng nhập với user mặc định, trả về token.
     */
    protected String registerAndLoginDefaultUser() throws Exception {
        return registerAndLogin("testuser", "testuser@example.com", "password123");
    }
}
