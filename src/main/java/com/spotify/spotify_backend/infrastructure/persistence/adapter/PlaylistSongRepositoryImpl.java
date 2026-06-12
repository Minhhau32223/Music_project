package com.spotify.spotify_backend.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.spotify.spotify_backend.domain.model.PlaylistSong;
import com.spotify.spotify_backend.domain.repository.PlaylistSongRepository;
import com.spotify.spotify_backend.infrastructure.persistence.entity.PlaylistSongEntity;
import com.spotify.spotify_backend.infrastructure.persistence.repository.PlaylistSongJpaRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PlaylistSongRepositoryImpl
        implements PlaylistSongRepository {

    private final PlaylistSongJpaRepository repository;

    @Override
    public PlaylistSong save(
            PlaylistSong playlistSong
    ) {

        PlaylistSongEntity entity =
                PlaylistSongEntity.builder()
                        .id(playlistSong.getId())
                        .playlistId(
                                playlistSong.getPlaylistId()
                        )
                        .songId(
                                playlistSong.getSongId()
                        )
                        .addedAt(
                                playlistSong.getAddedAt()
                        )
                        .build();

        PlaylistSongEntity saved =
                repository.save(entity);

        return mapToDomain(saved);
    }

    @Override
    public List<PlaylistSong> findByPlaylistId(
            Long playlistId
    ) {

        return repository.findByPlaylistId(
                        playlistId
                )
                .stream()
                .map(this::mapToDomain)
                .toList();
    }

    @Override
    public Optional<PlaylistSong>
    findByPlaylistIdAndSongId(
            Long playlistId,
            Long songId
    ) {

        return repository
                .findByPlaylistIdAndSongId(
                        playlistId,
                        songId
                )
                .map(this::mapToDomain);
    }

    @Override
    public void deleteByPlaylistIdAndSongId(
            Long playlistId,
            Long songId
    ) {

        repository.deleteByPlaylistIdAndSongId(
                playlistId,
                songId
        );
    }

    private PlaylistSong mapToDomain(
            PlaylistSongEntity entity
    ) {

        return PlaylistSong.builder()
                .id(entity.getId())
                .playlistId(entity.getPlaylistId())
                .songId(entity.getSongId())
                .addedAt(entity.getAddedAt())
                .build();
    }
}