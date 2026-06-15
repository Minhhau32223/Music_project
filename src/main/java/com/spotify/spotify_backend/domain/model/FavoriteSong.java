package com.spotify.spotify_backend.domain.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteSong {

    private Long id;

    private Long userId;

    private Long songId;

    private LocalDateTime createdAt;
}