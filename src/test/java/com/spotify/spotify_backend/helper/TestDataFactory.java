package com.spotify.spotify_backend.helper;

import com.spotify.spotify_backend.application.dto.Request.ChangePasswordRequest;
import com.spotify.spotify_backend.application.dto.Request.LoginRequest;
import com.spotify.spotify_backend.application.dto.Request.RegisterRequest;
import com.spotify.spotify_backend.application.dto.Request.UpdateProfileRequest;

/**
 * Factory cho test data.
 * Luôn dùng các phương thức ở đây để tạo request objects.
 * Đảm bảo khớp với các Java Records của project.
 */
public class TestDataFactory {

    // ======== AUTH TEST DATA ========

    public static RegisterRequest validRegisterRequest() {
        return new RegisterRequest("testuser", "testuser@example.com", "password123");
    }

    public static RegisterRequest registerRequest(String username, String email) {
        return new RegisterRequest(username, email, "password123");
    }

    public static LoginRequest validLoginRequest() {
        return new LoginRequest("testuser@example.com", "password123");
    }

    public static LoginRequest loginRequest(String email, String password) {
        return new LoginRequest(email, password);
    }

    // ======== USER UPDATE TEST DATA ========

    public static UpdateProfileRequest validUpdateProfileRequest() {
        return new UpdateProfileRequest("updateduser", "updated@example.com");
    }

    public static ChangePasswordRequest validChangePasswordRequest() {
        return new ChangePasswordRequest("password123", "newpassword456");
    }

    public static ChangePasswordRequest changePasswordRequest(String current, String newPass) {
        return new ChangePasswordRequest(current, newPass);
    }

    // ======== INVALID TEST DATA ========

    public static RegisterRequest registerRequestMissingUsername() {
        return new RegisterRequest("", "test@example.com", "password123");
    }

    public static RegisterRequest registerRequestInvalidEmail() {
        return new RegisterRequest("testuser", "not-an-email", "password123");
    }

    public static RegisterRequest registerRequestShortPassword() {
        return new RegisterRequest("testuser", "test@example.com", "abc");
    }
}
