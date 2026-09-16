package com.spotify.spotify_backend.infrastructure.persistence.entity;

import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedDate;
import com.spotify.spotify_backend.domain.model.ArtistApplicationStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "artist_applications") @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class ArtistApplicationEntity {
 @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
 @Column(nullable=false) private Long userId;
 @Column(nullable=false) private String artistName;
 @Column(nullable=false, columnDefinition="TEXT") private String bio;
 @Column(nullable=false, length=2048) private String profileImage;
 @Enumerated(EnumType.STRING) @Column(nullable=false) private ArtistApplicationStatus status;
 @Column(nullable=false) private LocalDateTime submittedAt;
 private LocalDateTime reviewedAt; private Long reviewedBy;
 @Column(columnDefinition="TEXT") private String rejectionReason;
 @PrePersist void created(){ if(submittedAt==null) submittedAt=LocalDateTime.now(); if(status==null) status=ArtistApplicationStatus.PENDING; }
}
