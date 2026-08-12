package com.spotify.spotify_backend.application.usecase.searchusecase;

import com.spotify.spotify_backend.application.dto.Request.SongFilterRequest;
import com.spotify.spotify_backend.application.dto.Response.PageResponse;
import com.spotify.spotify_backend.application.dto.Response.SongResponse;

public interface FilterSongsUseCase {
    PageResponse<SongResponse> execute(SongFilterRequest request);
}
