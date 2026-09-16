package com.spotify.spotify_backend.infrastructure.persistence.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
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
    @Column(nullable = false, columnDefinition = "TEXT")
    private String lyrics;
    @Column(nullable = false)
    private String duration;
    @Column(nullable = false)
    private Long playCount;
    @Column(nullable = false)
    private Long albumId;
    @Column(nullable = false)
    private Long artistId;

    /**
     * Publication state is owned by the creator workflow. Catalog writes in this
     * adapter must not accidentally reset it, so it is intentionally read-only.
     */
    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.updatedAt == null) this.updatedAt = this.createdAt;
        if (this.status == null) this.status = "PUBLISHED";
    }

    @PreUpdate
    protected void onUpdate() { this.updatedAt = LocalDateTime.now(); }
}
