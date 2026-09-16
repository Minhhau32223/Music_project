package com.spotify.spotify_backend.domain.model;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Builder
public class ArtistApplication {
    private Long id; private Long userId; private String artistName; private String bio; private String profileImage;
    private ArtistApplicationStatus status; private LocalDateTime submittedAt; private LocalDateTime reviewedAt;
    private Long reviewedBy; private String rejectionReason;
}
