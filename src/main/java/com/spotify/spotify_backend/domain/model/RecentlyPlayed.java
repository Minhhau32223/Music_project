package com.spotify.spotify_backend.domain.model;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RecentlyPlayed {

    private Long id;

    private Long userId;

    private Long songId;
    
    private LocalDateTime playedAt;
}