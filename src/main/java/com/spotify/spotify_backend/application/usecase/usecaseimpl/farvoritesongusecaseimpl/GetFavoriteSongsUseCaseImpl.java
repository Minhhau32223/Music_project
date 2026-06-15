package com.spotify.spotify_backend.application.usecase.usecaseimpl.farvoritesongusecaseimpl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.spotify.spotify_backend.application.dto.Response.SongResponse;
import com.spotify.spotify_backend.application.mapper.SongMapper;
import com.spotify.spotify_backend.application.usecase.farvoritesongusecase.GetFavoriteSongsUseCase;
import com.spotify.spotify_backend.domain.repository.AlbumRepository;
import com.spotify.spotify_backend.domain.repository.ArtistRepository;
import com.spotify.spotify_backend.domain.repository.FavoriteSongRepository;
import com.spotify.spotify_backend.domain.repository.SongRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetFavoriteSongsUseCaseImpl
        implements GetFavoriteSongsUseCase {

    private final FavoriteSongRepository
            favoriteSongRepository;

    private final SongRepository songRepository;

    private final AlbumRepository albumRepository;

    private final ArtistRepository artistRepository;

    @Override
    public List<SongResponse> execute(
            Long userId
    ) {

        return favoriteSongRepository
                .findByUserId(userId)
                .stream()
                .map(item ->
                        songRepository
                                .findById(
                                        item.getSongId()
                                )
                                .orElseThrow()
                )
                .map(song -> {

                    String albumName =
                            albumRepository
                                    .findById(
                                            song.getAlbumId()
                                    )
                                    .map(a -> a.getName())
                                    .orElse("");

                    String artistName =
                            artistRepository
                                    .findById(
                                            song.getArtistId()
                                    )
                                    .map(a -> a.getName())
                                    .orElse("");

                    return SongMapper.toResponse(
                            song,
                            albumName,
                            artistName
                    );
                })
                .toList();
    }
}