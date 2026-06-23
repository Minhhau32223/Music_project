package com.spotify.spotify_backend.application.usecase.recentlyplayedusecase;

import com.spotify.spotify_backend.application.dto.Response.RecentlyPlayedResponse;

import java.util.List;

public interface GetRecentlyPlayedUseCase {
 List<RecentlyPlayedResponse> execute(
            Long userId
    );
}
