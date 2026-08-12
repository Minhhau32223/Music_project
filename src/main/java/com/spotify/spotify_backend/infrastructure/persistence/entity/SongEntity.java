package com.spotify.spotify_backend.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "songs")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SongEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String file;
    @Column(nullable = false)
    private String image;
    @Column(nullable = false)
    private String lyrics;
    @Column(nullable = false)
    private String duration;
    @Column(nullable = false)
    private Long playCount;
    @Column(nullable = false)
    private Long albumId;
    @Column(nullable = false)
    private Long artistId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private java.time.LocalDateTime createdAt;

    @jakarta.persistence.PrePersist
    protected void onCreate() {
        this.createdAt = java.time.LocalDateTime.now();
    }
}
