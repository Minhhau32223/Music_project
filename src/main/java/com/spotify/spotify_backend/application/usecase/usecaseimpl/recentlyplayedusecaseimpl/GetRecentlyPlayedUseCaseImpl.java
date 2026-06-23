package com.spotify.spotify_backend.application.usecase.usecaseimpl.recentlyplayedusecaseimpl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.spotify.spotify_backend.application.dto.Response.RecentlyPlayedResponse;
import com.spotify.spotify_backend.application.usecase.recentlyplayedusecase.GetRecentlyPlayedUseCase;
import com.spotify.spotify_backend.domain.repository.ArtistRepository;
import com.spotify.spotify_backend.domain.repository.RecentlyPlayedRepository;
import com.spotify.spotify_backend.domain.repository.SongRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetRecentlyPlayedUseCaseImpl
        implements GetRecentlyPlayedUseCase {

    private final RecentlyPlayedRepository
            recentlyPlayedRepository;

    private final SongRepository songRepository;

    private final ArtistRepository artistRepository;

    @Override
    public List<RecentlyPlayedResponse> execute(
            Long userId
    ) {

        return recentlyPlayedRepository
                .findByUserId(
                        userId
                )
                .stream()
                .map(item -> {

                    var song =
                            songRepository
                                    .findById(
                                            item.getSongId()
                                    )
                                    .orElseThrow();

                    var artist =
                            artistRepository
                                    .findById(
                                            song.getArtistId()
                                    )
                                    .orElseThrow();

                    return new RecentlyPlayedResponse(
                            song.getId(),
                            song.getName(),
                            song.getImagePath(),
                            artist.getName(),
                            item.getPlayedAt()
                    );
                })
                .toList();
    }
}