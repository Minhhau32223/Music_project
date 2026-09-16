package com.spotify.spotify_backend.infrastructure.persistence.repository;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
import com.spotify.spotify_backend.domain.model.ArtistApplicationStatus;
import com.spotify.spotify_backend.infrastructure.persistence.entity.ArtistApplicationEntity;
public interface ArtistApplicationJpaRepository extends JpaRepository<ArtistApplicationEntity,Long> {
 List<ArtistApplicationEntity> findByUserIdOrderBySubmittedAtDesc(Long userId);
 List<ArtistApplicationEntity> findByStatusOrderBySubmittedAtAsc(ArtistApplicationStatus status);
 boolean existsByUserIdAndStatus(Long userId, ArtistApplicationStatus status);
}
