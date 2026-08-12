package com.spotify.spotify_backend.presentation.controller;

import com.spotify.spotify_backend.BaseIntegrationTest;
import com.spotify.spotify_backend.helper.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests cho UserController.
 * Tất cả endpoint đều yêu cầu JWT Authentication.
 * Pattern: Register → Login → Extract token → Gọi endpoint cần test.
 */
@DisplayName("UserController Tests")
class UserControllerTest extends BaseIntegrationTest {

    private static final String REGISTER_URL = "/api/auth/register/";
    private static final String LOGIN_URL = "/api/auth/login/";
    private static final String ME_URL = "/api/users/me/";
    private static final String PROFILE_URL = "/api/users/profile/";
    private static final String PASSWORD_URL = "/api/users/change-password/";
    private static final String DISABLE_URL = "/api/users/me/";

    private String userToken;

    /**
     * Trước mỗi test: đăng ký và đăng nhập để lấy token.
     */
    @BeforeEach
    void setUp() throws Exception {
        postRequest(REGISTER_URL, TestDataFactory.validRegisterRequest(), null);

        MvcResult loginResult = postRequest(LOGIN_URL, TestDataFactory.validLoginRequest(), null)
                .andExpect(status().isOk())
                .andReturn();

        userToken = extractToken(loginResult);
    }

    // ===================== GET ME =====================

    @Nested
    @DisplayName("GET /api/users/me/")
    class GetMeTests {

        @Test
        @DisplayName("TC-USER-01: Lấy thông tin user hiện tại thành công")
        void getMe_withValidToken_shouldReturn200() throws Exception {
            getRequest(ME_URL, userToken)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.username").value("testuser"))
                    .andExpect(jsonPath("$.email").value("testuser@example.com"));
        }

        @Test
        @DisplayName("TC-USER-02: Lấy thông tin user - không có JWT")
        void getMe_withoutToken_shouldReturn401() throws Exception {
            getRequest(ME_URL, null)
                    .andExpect(status().isUnauthorized());
        }
    }

    // ===================== UPDATE PROFILE =====================

    @Nested
    @DisplayName("PUT /api/users/profile/")
    class UpdateProfileTests {

        @Test
        @DisplayName("TC-USER-03: Cập nhật profile thành công")
        void updateProfile_withValidData_shouldReturn200() throws Exception {
            var request = TestDataFactory.validUpdateProfileRequest();

            putRequest(PROFILE_URL, request, userToken)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.username").value("updateduser"));
        }

        @Test
        @DisplayName("TC-USER-04: Cập nhật profile - không có JWT")
        void updateProfile_withoutToken_shouldReturn401() throws Exception {
            putRequest(PROFILE_URL, TestDataFactory.validUpdateProfileRequest(), null)
                    .andExpect(status().isUnauthorized());
        }
    }

    // ===================== CHANGE PASSWORD =====================

    @Nested
    @DisplayName("PUT /api/users/change-password/")
    class ChangePasswordTests {

        @Test
        @DisplayName("TC-USER-05: Đổi mật khẩu thành công")
        void changePassword_withValidCurrentPassword_shouldReturn200() throws Exception {
            var request = TestDataFactory.validChangePasswordRequest();

            putRequest(PASSWORD_URL, request, userToken)
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("TC-USER-06: Đổi mật khẩu thất bại - sai mật khẩu cũ")
        void changePassword_withWrongCurrentPassword_shouldReturn4xx() throws Exception {
            var request = TestDataFactory.changePasswordRequest("wrongpassword", "newpassword456");

            putRequest(PASSWORD_URL, request, userToken)
                    .andExpect(status().is4xxClientError());
        }

        @Test
        @DisplayName("TC-USER-07: Đổi mật khẩu - không có JWT")
        void changePassword_withoutToken_shouldReturn401() throws Exception {
            putRequest(PASSWORD_URL, TestDataFactory.validChangePasswordRequest(), null)
                    .andExpect(status().isUnauthorized());
        }
    }

    // ===================== DISABLE ACCOUNT =====================

    @Nested
    @DisplayName("DELETE /api/users/me/")
    class DisableAccountTests {

        @Test
        @DisplayName("TC-USER-08: Vô hiệu hóa tài khoản thành công")
        void disableAccount_withValidToken_shouldReturn204() throws Exception {
            deleteRequest(DISABLE_URL, userToken)
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("TC-USER-09: Vô hiệu hóa tài khoản - không có JWT")
        void disableAccount_withoutToken_shouldReturn401() throws Exception {
            deleteRequest(DISABLE_URL, null)
                    .andExpect(status().isUnauthorized());
        }
    }
}
