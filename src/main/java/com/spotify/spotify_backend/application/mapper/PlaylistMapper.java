package com.spotify.spotify_backend.application.mapper;

import com.spotify.spotify_backend.application.dto.Request.CreatePlaylistRequest;
import com.spotify.spotify_backend.application.dto.Response.PlaylistResponse;
import com.spotify.spotify_backend.domain.model.Playlist;

public class PlaylistMapper {

    public static Playlist toDomain(
            CreatePlaylistRequest request
    ) {

        return Playlist.builder()
                .name(request.name())
                .image(request.image())
                .build();
    }

    public static PlaylistResponse toResponse(
            Playlist playlist
    ) {

        return new PlaylistResponse(playlist.getId(), playlist.getName(),playlist.getImage(),playlist.getUserId());
             
    }
}
