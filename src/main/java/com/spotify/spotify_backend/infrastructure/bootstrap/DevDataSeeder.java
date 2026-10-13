package com.spotify.spotify_backend.infrastructure.bootstrap;

import java.time.LocalDateTime;
import java.util.function.Supplier;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
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
import com.spotify.spotify_backend.infrastructure.persistence.entity.SongEntity;
import com.spotify.spotify_backend.infrastructure.persistence.entity.FavoriteSongEntity;
import com.spotify.spotify_backend.infrastructure.persistence.entity.RecentlyPlayedEntity;
import com.spotify.spotify_backend.infrastructure.persistence.entity.UserEntity;
import com.spotify.spotify_backend.infrastructure.persistence.repository.AlbumJpaRepository;
import com.spotify.spotify_backend.infrastructure.persistence.repository.ArtistApplicationJpaRepository;
import com.spotify.spotify_backend.infrastructure.persistence.repository.ArtistFollowJpaRepository;
import com.spotify.spotify_backend.infrastructure.persistence.repository.ArtistJpaRepository;
import com.spotify.spotify_backend.infrastructure.persistence.repository.PlaylistJpaRepository;
import com.spotify.spotify_backend.infrastructure.persistence.repository.UserJpaRepository;
import com.spotify.spotify_backend.infrastructure.persistence.repository.SongJpaRepository;
import com.spotify.spotify_backend.infrastructure.persistence.repository.FavoriteSongJpaRepository;
import com.spotify.spotify_backend.infrastructure.persistence.repository.RecentlyPlayedJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** Local-only, idempotent scenario data. Media URLs are placeholders and contain no copyrighted audio. */
@Component
@Profile("dev")
@ConditionalOnProperty(prefix = "app.seed", name = "enabled", havingValue = "true")
@Order(0)
@RequiredArgsConstructor
@Slf4j
public class DevDataSeeder implements ApplicationRunner {
    private static final String IMAGE = "https://images.unsplash.com/photo-1516280440614-37939bbacd81?auto=format&fit=crop&w=800&q=80";
    private final UserJpaRepository users;
    private final SongJpaRepository songs;
    private final FavoriteSongJpaRepository favorites;
    private final RecentlyPlayedJpaRepository recentlyPlayed;
    private final ArtistJpaRepository artists;
    private final AlbumJpaRepository albums;
    private final ArtistApplicationJpaRepository applications;
    private final PlaylistJpaRepository playlists;
    private final ArtistFollowJpaRepository follows;
    private final PasswordHasher passwordHasher;

    @Override @Transactional
    public void run(ApplicationArguments args) {
        String password = System.getenv().getOrDefault("DEV_SEED_PASSWORD", "change-me-local");
        String adminPassword = System.getenv().getOrDefault("SEED_ADMIN_PASSWORD", password);
        UserEntity admin = user("admin@music.local", "admin-demo", Role.ADMIN, true, adminPassword);
        if (System.getenv().containsKey("SEED_ADMIN_PASSWORD")) {
            admin.setPasswordHash(passwordHasher.hashPassword(adminPassword));
            users.save(admin);
        }
        UserEntity listener = user("listener@music.local", "listener-demo", Role.USER, true, password);
        UserEntity coldStart = user("cold@music.local", "cold-demo", Role.USER, true, password);
        user("disabled@music.local", "disabled-demo", Role.USER, false, password);
        UserEntity artistAUser = user("artist.a@music.local", "artist-a-demo", Role.ARTIST, true, password);
        UserEntity artistBUser = user("artist.b@music.local", "artist-b-demo", Role.ARTIST, true, password);
        UserEntity applicant = user("applicant@music.local", "applicant-demo", Role.USER, true, password);
        UserEntity rejected = user("rejected@music.local", "rejected-demo", Role.USER, true, password);
        UserEntity applicantTwo = user("applicant.two@music.local", "applicant-two-demo", Role.USER, true, password);

        ArtistEntity artistA = artist(artistAUser, "Minh An", "Vietnamese pop and acoustic artist.");
        ArtistEntity artistB = artist(artistBUser, "Luna Park", "Indie singer and producer.");
        UserEntity artistCUser = user("artist.c@music.local", "artist-c-demo", Role.ARTIST, true, password);
        ArtistEntity artistC = artist(artistCUser, "River North", "Electronic and alternative artist.");
        AlbumEntity neon = album(artistA, "Neon After Rain", "PUBLISHED", null, 30);
        AlbumEntity unfinished = album(artistA, "Unfinished Letters", "DRAFT", null, 2);
        AlbumEntity moonlit = album(artistB, "Moonlit Rooms", "PUBLISHED", null, 14);
        AlbumEntity studio = album(artistB, "Studio Review", "PENDING_REVIEW", null, 1);
        AlbumEntity oldVersion = album(artistB, "Old Version", "REJECTED", "Cover art needs replacement", 4);
        AlbumEntity river = album(artistC, "River Signals", "PUBLISHED", null, 20);
        AlbumEntity riverDraft = album(artistC, "Unreleased Current", "DRAFT", null, 3);
        AlbumEntity riverArchive = album(artistC, "Archive Room", "DISABLED", null, 40);
        application(applicant, "Daybreak", ArtistApplicationStatus.PENDING, null, null);
        application(rejected, "Echo Reed", ArtistApplicationStatus.REJECTED, admin.getId(), "Please provide a clearer artist bio.");
        application(applicantTwo, "Silver Static", ArtistApplicationStatus.PENDING, null, null);
        playlist(listener, "Chill Night"); playlist(listener, "Weekend Queue"); playlist(coldStart, "Start Here");
        follow(listener, artistA); follow(listener, artistB); follow(coldStart, artistA);
        seedSongs(artistA, neon, unfinished, "Neon");
        seedSongs(artistB, moonlit, studio, "Moon");
        seedSongs(artistC, river, riverDraft, "River");
        seedInteractions(listener, coldStart);
        log.info("Development scenario seed completed with synthetic catalog data");
    }

    private UserEntity user(String email, String username, Role role, boolean active, String password) {
        return users.findByEmail(email).orElseGet(() -> users.save(UserEntity.builder().email(email).username(username)
                .role(role).active(active).passwordHash(passwordHasher.hashPassword(password)).avatarUrl(IMAGE).build()));
    }
    private ArtistEntity artist(UserEntity user, String name, String bio) {
        return artists.findByUserId(user.getId()).orElseGet(() -> artists.save(ArtistEntity.builder().userId(user.getId()).name(name).bio(bio).profileImage(IMAGE).build()));
    }
    private AlbumEntity album(ArtistEntity artist, String name, String status, String reason, int daysAgo) {
        var existing = albums.findByName(name);
        if (existing.isPresent()) return existing.get();
        LocalDateTime time = LocalDateTime.now().minusDays(daysAgo);
        return albums.save(AlbumEntity.builder().artistId(artist.getId()).name(name).image(IMAGE).description("Development seed album").bgColor("#1f1b2e")
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

    private void seedSongs(ArtistEntity artist, AlbumEntity publishedAlbum, AlbumEntity secondaryAlbum, String prefix) {
        for (int index = 1; index <= 10; index++) {
            String name = prefix + " Demo Track " + index;
            if (songs.findByNameContainingIgnoreCase(name).stream().anyMatch(song -> name.equals(song.getName()))) continue;
            String status = switch (index) {
                case 8 -> "DRAFT";
                case 9 -> "PENDING_REVIEW";
                case 10 -> "REJECTED";
                default -> "PUBLISHED";
            };
            if ("River".equals(prefix) && index == 7) status = "DISABLED";
            LocalDateTime created = LocalDateTime.now().minusDays(index);
            songs.save(SongEntity.builder().name(name).file("https://example.invalid/audio/" + name.replace(' ', '-') + ".mp3")
                    .image(IMAGE).lyrics("Synthetic development lyrics for " + name).duration(String.format("%02d:%02d", 2 + index / 6, (index * 7) % 60))
                    .playCount((long) (index * 13)).albumId("PUBLISHED".equals(status) ? publishedAlbum.getId() : secondaryAlbum.getId()).artistId(artist.getId())
                    .status(status).rejectionReason("REJECTED".equals(status) ? "Development rejection example" : null)
                    .createdAt(created).updatedAt(created).publishedAt("PUBLISHED".equals(status) ? created : null).build());
        }
    }

    private void seedInteractions(UserEntity listener, UserEntity coldStart) {
        var neon = songs.findByNameContainingIgnoreCase("Neon Demo Track 1").stream().findFirst().orElse(null);
        var moon = songs.findByNameContainingIgnoreCase("Moon Demo Track 1").stream().findFirst().orElse(null);
        var river = songs.findByNameContainingIgnoreCase("River Demo Track 1").stream().findFirst().orElse(null);
        if (neon != null) favorite(listener, neon.getId());
        if (moon != null) favorite(listener, moon.getId());
        if (neon != null) played(listener.getId(), neon.getId(), 1);
        if (moon != null) played(listener.getId(), moon.getId(), 2);
        if (river != null) played(coldStart.getId(), river.getId(), 1);
    }

    private void favorite(UserEntity user, Long songId) {
        if (favorites.findByUserIdAndSongId(user.getId(), songId).isEmpty()) {
            favorites.save(FavoriteSongEntity.builder().userId(user.getId()).songId(songId)
                    .createdAt(LocalDateTime.now().minusDays(1)).build());
        }
    }

    private void played(Long userId, Long songId, int daysAgo) {
        if (recentlyPlayed.findByUserIdOrderByPlayedAtDesc(userId).stream().noneMatch(item -> songId.equals(item.getSongId()))) {
            recentlyPlayed.save(RecentlyPlayedEntity.builder().userId(userId).songId(songId)
                    .playedAt(LocalDateTime.now().minusDays(daysAgo)).build());
        }
    }
}
