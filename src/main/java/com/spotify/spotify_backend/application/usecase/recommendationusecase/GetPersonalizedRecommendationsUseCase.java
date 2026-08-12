package com.spotify.spotify_backend.application.usecase.recommendationusecase;

import java.util.List;
import com.spotify.spotify_backend.application.dto.Response.SongResponse;

public interface GetPersonalizedRecommendationsUseCase {
    List<SongResponse> execute(Long userId, int limit);
}
