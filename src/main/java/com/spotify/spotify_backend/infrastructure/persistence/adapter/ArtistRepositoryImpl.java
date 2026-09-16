package com.spotify.spotify_backend.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.transaction.annotation.Transactional;

import com.spotify.spotify_backend.domain.model.Artist;
import com.spotify.spotify_backend.domain.repository.ArtistRepository;
import com.spotify.spotify_backend.infrastructure.persistence.entity.ArtistEntity;
import com.spotify.spotify_backend.infrastructure.persistence.repository.ArtistJpaRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ArtistRepositoryImpl implements ArtistRepository {

    private final ArtistJpaRepository artistJpaRepository;

    @Override
    @Transactional
    @CacheEvict(cacheNames = {"topSongs", "newReleases"}, allEntries = true)
    public Artist save(Artist artist) {

        ArtistEntity entity = artist.getId() == null
                ? ArtistEntity.builder().build()
                : artistJpaRepository.findById(artist.getId()).orElseThrow();
        entity.setName(artist.getName());
        entity.setBio(artist.getBio());
        entity.setProfileImage(artist.getProfileImage());

        ArtistEntity savedEntity = artistJpaRepository.save(entity);

        return mapToDomain(savedEntity);
    }

    @Override
    public Optional<Artist> findById(Long id) {
        return artistJpaRepository.findById(id)
                .map(this::mapToDomain);
    }

    @Override
    public List<Artist> findAll() {
        return artistJpaRepository.findAll()
                .stream()
                .map(this::mapToDomain)
                .toList();
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = {"topSongs", "newReleases"}, allEntries = true)
    public void deleteById(Long id) {
        artistJpaRepository.deleteById(id);
    }
    @Override
    public List<Artist> findByNameContaining(
            String keyword
    ) {

        return artistJpaRepository
                .findByNameContainingIgnoreCase(keyword)
                .stream()
                .map(this::mapToDomain)
                .toList();
    }

    private Artist mapToDomain(ArtistEntity entity) {

        Artist artist = new Artist();

        artist.setId(entity.getId());
        artist.setName(entity.getName());
        artist.setBio(entity.getBio());
        artist.setProfileImage(entity.getProfileImage());

        return artist;
    }
}
