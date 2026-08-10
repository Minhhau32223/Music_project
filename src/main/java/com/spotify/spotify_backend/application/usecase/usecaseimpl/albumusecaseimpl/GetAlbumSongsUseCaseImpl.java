package com.spotify.spotify_backend.application.usecase.usecaseimpl.albumusecaseimpl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.spotify.spotify_backend.application.dto.Response.SongResponse;
import com.spotify.spotify_backend.application.mapper.SongMapper;
import com.spotify.spotify_backend.application.usecase.albumusecase.GetAlbumSongsUseCase;
import com.spotify.spotify_backend.domain.exception.NotFoundException;
import com.spotify.spotify_backend.domain.model.Album;
import com.spotify.spotify_backend.domain.model.Artist;
import com.spotify.spotify_backend.domain.repository.AlbumRepository;
import com.spotify.spotify_backend.domain.repository.ArtistRepository;
import com.spotify.spotify_backend.domain.repository.SongRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetAlbumSongsUseCaseImpl implements GetAlbumSongsUseCase {

    private final SongRepository songRepository;
    private final AlbumRepository albumRepository;
    private final ArtistRepository artistRepository;

    @Override
    public List<SongResponse> execute(Long albumId) {

        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new NotFoundException("Album not found"));

        return songRepository.findByAlbumId(albumId)
                .stream()
                .map(song -> {

                    Artist artist = artistRepository.findById(song.getArtistId())
                            .orElse(null);

                    return SongMapper.toResponse(
                            song,
                            album.getName(),
                            artist != null ? artist.getName() : null
                    );
                })
                .toList();
    }
}