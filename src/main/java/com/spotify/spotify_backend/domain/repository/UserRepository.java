package com.spotify.spotify_backend.domain.repository;
import java.util.Optional;

import com.spotify.spotify_backend.domain.model.User;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(Long id);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
}
