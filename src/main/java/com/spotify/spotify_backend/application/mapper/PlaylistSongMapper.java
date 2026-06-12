package com.spotify.spotify_backend.application.mapper;

import com.spotify.spotify_backend.application.dto.Response.PlaylistSongResponse;
import com.spotify.spotify_backend.domain.model.PlaylistSong;

public class PlaylistSongMapper {

 public static PlaylistSongResponse toDomain(
            PlaylistSong playlistSong
    ) {

        return PlaylistSongResponse.builder()
                .id(playlistSong.getId())
                .playlistId(
                        playlistSong.getPlaylistId()
                )
                .songId(
                        playlistSong.getSongId()
                )
                .build();
    }
     public static PlaylistSongResponse toResponse(
            PlaylistSong playlistSong
    ) {

        return new PlaylistSongResponse(playlistSong.getId(), playlistSong.getPlaylistId(),playlistSong.getSongId());
    }
}
