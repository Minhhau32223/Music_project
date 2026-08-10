package com.spotify.spotify_backend.infrastructure.persistence.adapter;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.spotify.spotify_backend.domain.model.User;
import com.spotify.spotify_backend.domain.repository.UserRepository;
import com.spotify.spotify_backend.infrastructure.persistence.entity.UserEntity;
import com.spotify.spotify_backend.infrastructure.persistence.repository.UserJpaRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    @Override
    public User save(User user) {

        UserEntity entity = UserEntity.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .passwordHash(user.getPasswordHash())
                .active(user.getActive())
                .role(user.getRole())
                .avatarUrl(user.getAvatarUrl())
                .build();

        UserEntity saved = userJpaRepository.save(entity);

        return mapToDomain(saved);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userJpaRepository.findById(id)
                .map(this::mapToDomain);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userJpaRepository.findByUsername(username)
                .map(this::mapToDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(email)
                .map(this::mapToDomain);
    }

    @Override
    public Boolean existsByUsername(String username) {
        return userJpaRepository.existsByUsername(username);
    }

    @Override
    public Boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }

    private User mapToDomain(UserEntity entity) {

        return User.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .passwordHash(entity.getPasswordHash())
                .active(entity.getActive())
                .role(entity.getRole())
                .avatarUrl(entity.getAvatarUrl())
                .build();
    }

}
