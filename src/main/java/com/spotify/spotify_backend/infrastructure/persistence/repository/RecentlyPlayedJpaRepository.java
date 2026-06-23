package com.spotify.spotify_backend.infrastructure.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.spotify.spotify_backend.infrastructure.persistence.entity.RecentlyPlayedEntity;

import jakarta.transaction.Transactional;

public interface RecentlyPlayedJpaRepository
        extends JpaRepository<
                RecentlyPlayedEntity,
                Long> {

    List<RecentlyPlayedEntity>
    findByUserIdOrderByPlayedAtDesc(
            Long userId
    );

    long countByUserId(
            Long userId
    );
@Modifying
@Transactional
@Query(
        value = """
                DELETE FROM recently_played
                WHERE id = (
                    SELECT id
                    FROM recently_played
                    WHERE user_id = :userId
                    ORDER BY played_at ASC
                    LIMIT 1
                )
                """,
        nativeQuery = true
)
void deleteOldestByUserId(
        @Param("userId")
        Long userId
);


}