package com.spotify.spotify_backend.application.usecase.usecaseimpl.recommendationusecaseimpl;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.spotify.spotify_backend.application.dto.Response.SongResponse;
import com.spotify.spotify_backend.application.mapper.SongMapper;
import com.spotify.spotify_backend.application.usecase.QueryLimits;
import  com.spotify.spotify_backend.application.usecase.recommendationusecase.GetTopSongsUseCase;
import com.spotify.spotify_backend.domain.model.Album;
import com.spotify.spotify_backend.domain.model.Artist;
import com.spotify.spotify_backend.domain.model.Song;
import com.spotify.spotify_backend.domain.repository.AlbumRepository;
import com.spotify.spotify_backend.domain.repository.ArtistRepository;
import com.spotify.spotify_backend.domain.repository.SongRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetTopSongsUseCaseImpl implements GetTopSongsUseCase {

    private final SongRepository songRepository;
    private final ArtistRepository artistRepository;
    private final AlbumRepository albumRepository;

    @Override
    @Cacheable(cacheNames = "topSongs", key = "#limit",
            condition = "#limit >= 1 && #limit <= 100")
    public List<SongResponse> execute(int limit) {
       QueryLimits.validateLimit(limit);
        List<Song> songs = songRepository.findTopSongs(limit);
        return songs.stream()
                .map(song -> {
                    Artist artist = artistRepository.findById(song.getArtistId()).orElse(null);
                    Album album = albumRepository.findById(song.getAlbumId()).orElse(null);
                    return SongMapper.toResponse(
                            song,
                            album != null ? album.getName() : "",
                            artist != null ? artist.getName() : ""
                    );
                })
                .toList();
    }
}
