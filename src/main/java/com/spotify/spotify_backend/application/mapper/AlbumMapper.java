package com.spotify.spotify_backend.application.mapper;

import com.spotify.spotify_backend.application.dto.Request.CreateAlbumRequest;
import com.spotify.spotify_backend.application.dto.Response.AlbumResponse;
import com.spotify.spotify_backend.domain.model.Album;

public class AlbumMapper {

    private AlbumMapper() {}

    public static Album toDomain(CreateAlbumRequest request) {

        Album album = new Album();

        album.setName(request.name());
        album.setImage(request.image());
        album.setDescription(request.description());
        album.setBgColor(request.bgColor());

        return album;
    }

    public static AlbumResponse toResponse(Album album) {

        return new AlbumResponse(
                album.getId(),
                album.getName(),
                album.getImage(),
                album.getDescription(),
                album.getBgColor()
        );
    }
}