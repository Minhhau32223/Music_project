package com.spotify.spotify_backend.application.usecase.usecaseimpl.playlistsongusecaseimpl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.spotify.spotify_backend.application.dto.Response.SongResponse;
import com.spotify.spotify_backend.application.mapper.SongMapper;
import com.spotify.spotify_backend.application.usecase.playplistsongusecase.GetPlaylistSongsUseCase;
import com.spotify.spotify_backend.domain.repository.AlbumRepository;
import com.spotify.spotify_backend.domain.repository.ArtistRepository;
import com.spotify.spotify_backend.domain.repository.PlaylistSongRepository;
import com.spotify.spotify_backend.domain.repository.SongRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetPlaylistSongsUseCaseImpl
        implements GetPlaylistSongsUseCase {

    private final PlaylistSongRepository
            playlistSongRepository;

    private final SongRepository songRepository;
    private final AlbumRepository albumRepository;
    private final ArtistRepository artistRepository;

    @Override
public List<SongResponse> execute(Long playlistId) {

    return playlistSongRepository
            .findByPlaylistId(playlistId)
            .stream()
            .map(item ->
                    songRepository
                            .findById(item.getSongId())
                            .orElseThrow()
            )
            .map(song -> {

                String albumName =
                        albumRepository
                                .findById(song.getAlbumId())
                                .map(album -> album.getName())
                                .orElse("");

                String artistName =
                        artistRepository
                                .findById(song.getArtistId())
                                .map(artist -> artist.getName())
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