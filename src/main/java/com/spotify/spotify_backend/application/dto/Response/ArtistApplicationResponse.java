package com.spotify.spotify_backend.application.dto.Response;
import java.time.LocalDateTime;
import com.spotify.spotify_backend.domain.model.ArtistApplicationStatus;
public record ArtistApplicationResponse(Long id, Long userId, String artistName, String bio, String profileImage, ArtistApplicationStatus status, LocalDateTime submittedAt, LocalDateTime reviewedAt, Long reviewedBy, String rejectionReason) {}
