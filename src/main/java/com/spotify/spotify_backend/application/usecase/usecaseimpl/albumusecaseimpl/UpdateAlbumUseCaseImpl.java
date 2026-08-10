package com.spotify.spotify_backend.application.usecase.usecaseimpl.albumusecaseimpl;

import org.springframework.stereotype.Service;

import com.spotify.spotify_backend.application.dto.Request.UpdateAlbumRequest;
import com.spotify.spotify_backend.application.dto.Response.AlbumResponse;
import com.spotify.spotify_backend.application.mapper.AlbumMapper;
import com.spotify.spotify_backend.application.usecase.albumusecase.UpdateAlbumUseCase;
import com.spotify.spotify_backend.domain.exception.NotFoundException;
import com.spotify.spotify_backend.domain.model.Album;
import com.spotify.spotify_backend.domain.repository.AlbumRepository;
import com.spotify.spotify_backend.domain.repository.CloudinaryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UpdateAlbumUseCaseImpl implements UpdateAlbumUseCase {

    private final AlbumRepository albumRepository;
    private final CloudinaryRepository cloudinaryRepository;

    @Override
    public AlbumResponse execute(Long albumId, UpdateAlbumRequest request) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new NotFoundException("Album not found"));

        if (request.name() != null && !request.name().trim().isEmpty()) {
            album.setName(request.name());
        }
        if (request.description() != null) {
            album.setDescription(request.description());
        }
        if (request.bgColor() != null) {
            album.setBgColor(request.bgColor());
        }

        if (request.image() != null && !request.image().isEmpty()) {
            String imageUrl = cloudinaryRepository.uploadFile(request.image(), "album-images");
            album.setImage(imageUrl);
        }

        Album savedAlbum = albumRepository.save(album);

        return AlbumMapper.toResponse(savedAlbum);
    }
}
