package com.spotify.spotify_backend.application.usecase.albumusecase;

import java.util.List;

import com.spotify.spotify_backend.application.dto.Response.SongResponse;

public interface GetAlbumSongsUseCase {

    List<SongResponse> execute(Long albumId);

}