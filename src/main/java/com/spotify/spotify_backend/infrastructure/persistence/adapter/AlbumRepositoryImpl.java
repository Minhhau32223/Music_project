package com.spotify.spotify_backend.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.spotify.spotify_backend.domain.model.Album;
import com.spotify.spotify_backend.domain.repository.AlbumRepository;
import com.spotify.spotify_backend.infrastructure.persistence.entity.AlbumEntity;
import com.spotify.spotify_backend.infrastructure.persistence.repository.AlbumJpaRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AlbumRepositoryImpl implements AlbumRepository {
    private final AlbumJpaRepository albumJpaRepository;
    @Override
    public Album save(Album album){
        AlbumEntity entity=AlbumEntity.builder()
                                      .id(album.getId())
                                      .name(album.getName())
                                      .image(album.getImage())
                                      .description(album.getDescription())
                                      .bgColor(album.getBgColor())
                                      .build();
        AlbumEntity saveEntity= albumJpaRepository.save(entity);
        return mapToDomain(saveEntity);

    }
   

    @Override
    public Optional<Album> findById(Long id) {
        return albumJpaRepository.findById(id)
                .map(this::mapToDomain);
    }

    @Override
    public List<Album> findAll() {
        return albumJpaRepository.findAll()
                .stream()
                .map(this::mapToDomain)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        albumJpaRepository.deleteById(id);
    }
    @Override
    public List<Album> findByNameContaining(
            String keyword
    ) {

        return albumJpaRepository
                .findByNameContainingIgnoreCase(keyword)
                .stream()
                .map(this::mapToDomain)
                .toList();
    }
    private Album mapToDomain(AlbumEntity entity) {

        Album album = new Album();

        album.setId(entity.getId());
        album.setName(entity.getName());
        album.setImage(entity.getImage());
        album.setDescription(entity.getDescription());
        album.setBgColor(entity.getBgColor());

        return album;
    }


}
