package com.spotify.spotify_backend.domain.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaylistSong {

    private Long id;

    private Long playlistId;

    private Long songId;

    private LocalDateTime addedAt;
}