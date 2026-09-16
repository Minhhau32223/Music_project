package com.spotify.spotify_backend.infrastructure.bootstrap;

import java.time.LocalDateTime;
import java.util.function.Supplier;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.spotify.spotify_backend.application.usecase.PasswordHasher;
import com.spotify.spotify_backend.domain.model.ArtistApplicationStatus;
import com.spotify.spotify_backend.domain.model.Role;
import com.spotify.spotify_backend.infrastructure.persistence.entity.AlbumEntity;
import com.spotify.spotify_backend.infrastructure.persistence.entity.ArtistApplicationEntity;
import com.spotify.spotify_backend.infrastructure.persistence.entity.ArtistEntity;
import com.spotify.spotify_backend.infrastructure.persistence.entity.ArtistFollowEntity;
import com.spotify.spotify_backend.infrastructure.persistence.entity.PlaylistEntity;
import com.spotify.spotify_backend.infrastructure.persistence.entity.UserEntity;
import com.spotify.spotify_backend.infrastructure.persistence.repository.AlbumJpaRepository;
import com.spotify.spotify_backend.infrastructure.persistence.repository.ArtistApplicationJpaRepository;
import com.spotify.spotify_backend.infrastructure.persistence.repository.ArtistFollowJpaRepository;
import com.spotify.spotify_backend.infrastructure.persistence.repository.ArtistJpaRepository;
import com.spotify.spotify_backend.infrastructure.persistence.repository.PlaylistJpaRepository;
import com.spotify.spotify_backend.infrastructure.persistence.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** Local-only, idempotent scenario data. Songs are deliberately never seeded. */
@Component
@Profile("dev")
@ConditionalOnProperty(prefix = "app.seed", name = "enabled", havingValue = "true")
@RequiredArgsConstructor
@Slf4j
public class DevDataSeeder implements ApplicationRunner {
    private static final String IMAGE = "https://images.unsplash.com/photo-1516280440614-37939bbacd81?auto=format&fit=crop&w=800&q=80";
    private final UserJpaRepository users;
    private final ArtistJpaRepository artists;
    private final AlbumJpaRepository albums;
    private final ArtistApplicationJpaRepository applications;
    private final PlaylistJpaRepository playlists;
    private final ArtistFollowJpaRepository follows;
    private final PasswordHasher passwordHasher;

    @Override @Transactional
    public void run(ApplicationArguments args) {
        String password = System.getenv().getOrDefault("DEV_SEED_PASSWORD", "change-me-local");
        UserEntity admin = user("admin@music.local", "admin-demo", Role.ADMIN, true, password);
        UserEntity listener = user("listener@music.local", "listener-demo", Role.USER, true, password);
        UserEntity coldStart = user("cold@music.local", "cold-demo", Role.USER, true, password);
        user("disabled@music.local", "disabled-demo", Role.USER, false, password);
        UserEntity artistAUser = user("artist.a@music.local", "artist-a-demo", Role.ARTIST, true, password);
        UserEntity artistBUser = user("artist.b@music.local", "artist-b-demo", Role.ARTIST, true, password);
        UserEntity applicant = user("applicant@music.local", "applicant-demo", Role.USER, true, password);
        UserEntity rejected = user("rejected@music.local", "rejected-demo", Role.USER, true, password);

        ArtistEntity artistA = artist(artistAUser, "Minh An", "Vietnamese pop and acoustic artist.");
        ArtistEntity artistB = artist(artistBUser, "Luna Park", "Indie singer and producer.");
        album(artistA, "Neon After Rain", "PUBLISHED", null, 30);
        album(artistA, "Unfinished Letters", "DRAFT", null, 2);
        album(artistB, "Moonlit Rooms", "PUBLISHED", null, 14);
        album(artistB, "Studio Review", "PENDING_REVIEW", null, 1);
        album(artistB, "Old Version", "REJECTED", "Cover art needs replacement", 4);
        application(applicant, "Daybreak", ArtistApplicationStatus.PENDING, null, null);
        application(rejected, "Echo Reed", ArtistApplicationStatus.REJECTED, admin.getId(), "Please provide a clearer artist bio.");
        playlist(listener, "Chill Night"); playlist(listener, "Weekend Queue"); playlist(coldStart, "Start Here");
        follow(listener, artistA); follow(listener, artistB); follow(coldStart, artistA);
        log.info("Development scenario seed completed (songs intentionally skipped)");
    }

    private UserEntity user(String email, String username, Role role, boolean active, String password) {
        return users.findByEmail(email).orElseGet(() -> users.save(UserEntity.builder().email(email).username(username)
                .role(role).active(active).passwordHash(passwordHasher.hashPassword(password)).avatarUrl(IMAGE).build()));
    }
    private ArtistEntity artist(UserEntity user, String name, String bio) {
        return artists.findByUserId(user.getId()).orElseGet(() -> artists.save(ArtistEntity.builder().userId(user.getId()).name(name).bio(bio).profileImage(IMAGE).build()));
    }
    private void album(ArtistEntity artist, String name, String status, String reason, int daysAgo) {
        if (albums.findByName(name).isPresent()) return;
        LocalDateTime time = LocalDateTime.now().minusDays(daysAgo);
        albums.save(AlbumEntity.builder().artistId(artist.getId()).name(name).image(IMAGE).description("Development seed album").bgColor("#1f1b2e")
                .status(status).rejectionReason(reason).createdAt(time).updatedAt(time).publishedAt("PUBLISHED".equals(status) ? time : null).build());
    }
    private void application(UserEntity user, String name, ArtistApplicationStatus status, Long reviewer, String reason) {
        if (!applications.findByUserIdOrderBySubmittedAtDesc(user.getId()).isEmpty()) return;
        applications.save(ArtistApplicationEntity.builder().userId(user.getId()).artistName(name).bio("Development application").profileImage(IMAGE)
                .status(status).submittedAt(LocalDateTime.now().minusDays(3)).reviewedBy(reviewer).reviewedAt(reviewer == null ? null : LocalDateTime.now().minusDays(1)).rejectionReason(reason).build());
    }
    private void playlist(UserEntity user, String name) {
        if (playlists.findByUserId(user.getId()).stream().anyMatch(p -> name.equals(p.getName()))) return;
        playlists.save(PlaylistEntity.builder().userId(user.getId()).name(name).image(IMAGE).build());
    }
    private void follow(UserEntity user, ArtistEntity artist) {
        if (follows.findByUserIdAndArtistId(user.getId(), artist.getId()).isEmpty()) follows.save(ArtistFollowEntity.builder().userId(user.getId()).artistId(artist.getId()).followedAt(LocalDateTime.now().minusDays(1)).build());
    }
}
