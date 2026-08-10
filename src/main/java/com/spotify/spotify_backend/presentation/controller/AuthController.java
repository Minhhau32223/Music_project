package com.spotify.spotify_backend.presentation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spotify.spotify_backend.application.dto.Request.LoginRequest;
import com.spotify.spotify_backend.application.dto.Request.RegisterRequest;
import com.spotify.spotify_backend.application.dto.Response.LoginResponse;
import com.spotify.spotify_backend.application.dto.Response.RegisterResponse;
import com.spotify.spotify_backend.application.usecase.RegisterUseCase;
import com.spotify.spotify_backend.application.usecase.usecaseimpl.LoginUsecaseImpl;
import com.spotify.spotify_backend.domain.exception.InvalidCredentialException;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final RegisterUseCase registerUseCase;
    private final LoginUsecaseImpl loginUsecaseImpl;
    @PostMapping("/register/")
    public ResponseEntity<?> register( @Valid @RequestBody RegisterRequest request) {
        try {
            RegisterResponse response = registerUseCase.register(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping("/login/")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            LoginResponse response = loginUsecaseImpl.execute(request);
            return ResponseEntity.ok(response);
        } catch (InvalidCredentialException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }
    
}
