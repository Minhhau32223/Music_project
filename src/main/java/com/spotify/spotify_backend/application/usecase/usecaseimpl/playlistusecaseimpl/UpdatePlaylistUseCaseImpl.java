package com.spotify.spotify_backend.application.usecase.usecaseimpl.playlistusecaseimpl;

import org.springframework.stereotype.Service;

import com.spotify.spotify_backend.application.dto.Request.UpdatePlaylistRequest;
import com.spotify.spotify_backend.application.dto.Response.PlaylistResponse;
import com.spotify.spotify_backend.application.mapper.PlaylistMapper;
import com.spotify.spotify_backend.application.usecase.playlistusecase.UpdatePlaylistUseCase;
import com.spotify.spotify_backend.domain.exception.ForbiddenException;
import com.spotify.spotify_backend.domain.exception.NotFoundException;
import com.spotify.spotify_backend.domain.model.Playlist;
import com.spotify.spotify_backend.domain.repository.CloudinaryRepository;
import com.spotify.spotify_backend.domain.repository.PlaylistRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UpdatePlaylistUseCaseImpl implements UpdatePlaylistUseCase {

    private final PlaylistRepository playlistRepository;
    private final CloudinaryRepository cloudinaryRepository;

    @Override
    public PlaylistResponse execute(Long userId, Long playlistId, UpdatePlaylistRequest request) {
        
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new NotFoundException("Playlist not found"));

        if (!playlist.getUserId().equals(userId)) {
            throw new ForbiddenException("You do not have permission to update this playlist");
        }

        if (request.name() != null && !request.name().trim().isEmpty()) {
            playlist.setName(request.name());
        }

        if (request.image() != null && !request.image().isEmpty()) {
            String imageUrl = cloudinaryRepository.uploadFile(request.image(), "playlist-covers");
            playlist.setImage(imageUrl);
        }

        Playlist savedPlaylist = playlistRepository.save(playlist);

        return PlaylistMapper.toResponse(savedPlaylist);
    }
}
