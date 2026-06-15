package com.spotify.spotify_backend.application.usecase.farvoritesongusecase;

import java.util.List;

import com.spotify.spotify_backend.application.dto.Response.SongResponse;


public interface GetFavoriteSongsUseCase {
 List<SongResponse> execute(
            Long userId
    );
}
