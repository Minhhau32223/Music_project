package com.spotify.spotify_backend.application.usecase.recommendationusecase;

import java.util.List;
import com.spotify.spotify_backend.application.dto.Response.SongResponse;

public interface GetNewReleasesUseCase {
    List<SongResponse> execute(int limit);
}
