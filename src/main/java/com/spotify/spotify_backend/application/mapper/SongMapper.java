package com.spotify.spotify_backend.application.mapper;

import com.spotify.spotify_backend.application.dto.Request.CreateSongRequest;
import com.spotify.spotify_backend.application.dto.Response.SongResponse;
import com.spotify.spotify_backend.domain.model.Song;

public class SongMapper {

    public SongMapper() {
    }
    public static Song toDomain(CreateSongRequest request){
        Song song = new Song();

        song.setName(request.name());
        song.setFilePath(request.file());
        song.setImagePath(request.image());
        song.setLyrics(request.lyrics());
        song.setDuration(request.duration());

        song.setPlayCount(0L);

        song.setAlbumId(request.albumId());
        song.setArtistId(request.artistId());

        return song;
    }
    public static SongResponse toResponse(
        Song song,
        String albumName,
        String artistName
) {

    return new SongResponse(
            song.getId(),
            song.getName(),
            song.getFilePath(),
            song.getImagePath(),
            song.getLyrics(),
            song.getDuration(),
            song.getPlayCount(),
            song.getAlbumId(),
            song.getArtistId(),
            albumName,
            artistName
    );
}
}
