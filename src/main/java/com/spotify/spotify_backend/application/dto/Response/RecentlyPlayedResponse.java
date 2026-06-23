package com.spotify.spotify_backend.application.dto.Response;

import java.time.LocalDateTime;

public record RecentlyPlayedResponse(

        Long songId,

        String songName,

        String image,

        String artistName,

        LocalDateTime playedAt
) {
}