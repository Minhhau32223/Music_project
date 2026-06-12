package com.spotify.spotify_backend.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.spotify.spotify_backend.domain.model.Playlist;
import com.spotify.spotify_backend.domain.repository.PlaylistRepository;
import com.spotify.spotify_backend.infrastructure.persistence.entity.PlaylistEntity;
import com.spotify.spotify_backend.infrastructure.persistence.repository.PlaylistJpaRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PlaylistRepositoryImpl
        implements PlaylistRepository {

    private final PlaylistJpaRepository repository;

    @Override
    public Playlist save(Playlist playlist) {

        PlaylistEntity entity =
                PlaylistEntity.builder()
                        .id(playlist.getId())
                        .name(playlist.getName())
                        .image(playlist.getImage())
                        .userId(playlist.getUserId())
                        .build();

        PlaylistEntity saved = repository.save(entity);

        return mapToDomain(saved);
    }

    @Override
    public Optional<Playlist> findById(Long id) {
        return repository.findById(id)
                .map(this::mapToDomain);
    }

    @Override
    public List<Playlist> findAll() {

        return repository.findAll()
                .stream()
                .map(this::mapToDomain)
                .toList();
    }

    @Override
    public List<Playlist> findByUserId(Long userId) {

        return repository.findByUserId(userId)
                .stream()
                .map(this::mapToDomain)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    private Playlist mapToDomain(
            PlaylistEntity entity
    ) {

        return Playlist.builder()
                .id(entity.getId())
                .name(entity.getName())
                .image(entity.getImage())
                .userId(entity.getUserId())
                .build();
    }
}