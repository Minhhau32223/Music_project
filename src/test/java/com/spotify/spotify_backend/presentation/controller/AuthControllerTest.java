package com.spotify.spotify_backend.presentation.controller;

import com.spotify.spotify_backend.BaseIntegrationTest;
import com.spotify.spotify_backend.helper.TestDataFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import jakarta.servlet.http.Cookie;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

/**
 * Integration tests cho AuthController.
 * Bao gồm: Register (POST /api/auth/register/) và Login (POST /api/auth/login/)
 * 
 * Mỗi test được rollback sau khi chạy (@Transactional trong BaseIntegrationTest).
 */
@DisplayName("AuthController Tests")
class AuthControllerTest extends BaseIntegrationTest {

    private static final String REGISTER_URL = "/api/auth/register/";
    private static final String LOGIN_URL = "/api/auth/login/";
    private static final String REFRESH_URL = "/api/auth/refresh/";
    private static final String LOGOUT_URL = "/api/auth/logout/";
    private static final String REFRESH_COOKIE = "nocturne_refresh";

    // ===================== REGISTER TESTS =====================

    @Nested
    @DisplayName("POST /api/auth/register/")
    class RegisterTests {

        @Test
        @DisplayName("TC-AUTH-01: Đăng ký thành công với dữ liệu hợp lệ")
        void register_withValidData_shouldReturn200() throws Exception {
            var request = TestDataFactory.validRegisterRequest();

            postRequest(REGISTER_URL, request, null)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.username").value("testuser"))
                    .andExpect(jsonPath("$.email").value("testuser@example.com"));
        }

        @Test
        @DisplayName("TC-AUTH-02: Đăng ký thất bại - email đã tồn tại")
        void register_withDuplicateEmail_shouldReturn409OrBadRequest() throws Exception {
            var request = TestDataFactory.validRegisterRequest();

            // Đăng ký lần đầu
            postRequest(REGISTER_URL, request, null)
                    .andExpect(status().isOk());

            // Đăng ký lần 2 cùng email
            postRequest(REGISTER_URL, request, null)
                    .andExpect(status().is4xxClientError());
        }

        @Test
        @DisplayName("TC-AUTH-03: Đăng ký thất bại - username bị bỏ trống")
        void register_withBlankUsername_shouldReturn400() throws Exception {
            var request = TestDataFactory.registerRequestMissingUsername();

            postRequest(REGISTER_URL, request, null)
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("TC-AUTH-04: Đăng ký thất bại - email không hợp lệ")
        void register_withInvalidEmail_shouldReturn400() throws Exception {
            var request = TestDataFactory.registerRequestInvalidEmail();

            postRequest(REGISTER_URL, request, null)
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("TC-AUTH-05: Đăng ký thất bại - mật khẩu quá ngắn")
        void register_withShortPassword_shouldReturn400() throws Exception {
            var request = TestDataFactory.registerRequestShortPassword();

            postRequest(REGISTER_URL, request, null)
                    .andExpect(status().isBadRequest());
        }
    }

    // ===================== LOGIN TESTS =====================

    @Nested
    @DisplayName("POST /api/auth/login/")
    class LoginTests {

        private void registerUser() throws Exception {
            postRequest(REGISTER_URL, TestDataFactory.validRegisterRequest(), null);
        }

        @Test
        @DisplayName("TC-AUTH-06: Đăng nhập thành công - trả về accessToken")
        void login_withValidCredentials_shouldReturn200WithToken() throws Exception {
            registerUser();

            var loginRequest = TestDataFactory.validLoginRequest();

            postRequest(LOGIN_URL, loginRequest, null)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accessToken").exists())
                    .andExpect(jsonPath("$.accessToken").isNotEmpty())
                    .andExpect(jsonPath("$.email").value("testuser@example.com"))
                    .andExpect(cookie().httpOnly(REFRESH_COOKIE, true));
        }

        @Test
        @DisplayName("TC-AUTH-07: Đăng nhập thất bại - sai mật khẩu")
        void login_withWrongPassword_shouldReturn401() throws Exception {
            registerUser();

            var loginRequest = TestDataFactory.loginRequest("testuser@example.com", "wrongpassword");

            postRequest(LOGIN_URL, loginRequest, null)
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("TC-AUTH-08: Đăng nhập thất bại - email không tồn tại")
        void login_withNonExistentEmail_shouldReturn401() throws Exception {
            var loginRequest = TestDataFactory.loginRequest("nobody@example.com", "password123");

            postRequest(LOGIN_URL, loginRequest, null)
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("TC-AUTH-09: Đăng nhập thất bại - email không hợp lệ")
        void login_withInvalidEmailFormat_shouldReturn400() throws Exception {
            var loginRequest = TestDataFactory.loginRequest("not-an-email", "password123");

            postRequest(LOGIN_URL, loginRequest, null)
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Refresh and logout session")
    class SessionTests {

        @Test
        @DisplayName("TC-AUTH-10: Refresh cookie hợp lệ trả về access token mới")
        void refresh_withValidCookie_shouldReturnAccessToken() throws Exception {
            postRequest(REGISTER_URL, TestDataFactory.validRegisterRequest(), null);
            var login = postRequest(LOGIN_URL, TestDataFactory.validLoginRequest(), null)
                    .andExpect(status().isOk())
                    .andReturn();
            Cookie refreshCookie = login.getResponse().getCookie(REFRESH_COOKIE);

            mockMvc.perform(post(REFRESH_URL).cookie(refreshCookie))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accessToken").isNotEmpty());
        }

        @Test
        @DisplayName("TC-AUTH-11: Thiếu refresh cookie trả về 401")
        void refresh_withoutCookie_shouldReturn401() throws Exception {
            mockMvc.perform(post(REFRESH_URL))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("TC-AUTH-12: Logout xóa refresh cookie")
        void logout_shouldClearRefreshCookie() throws Exception {
            mockMvc.perform(post(LOGOUT_URL))
                    .andExpect(status().isNoContent())
                    .andExpect(cookie().maxAge(REFRESH_COOKIE, 0))
                    .andExpect(cookie().httpOnly(REFRESH_COOKIE, true));
        }
    }
}
