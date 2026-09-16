package com.spotify.spotify_backend.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.spotify.spotify_backend.infrastructure.security.JwtAccessDeniedHandler;
import com.spotify.spotify_backend.infrastructure.security.JwtAuthenticationEntryPoint;
import com.spotify.spotify_backend.presentation.middleware.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
            .cors(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS))

            .authorizeHttpRequests(auth -> auth
                // PUBLIC
                .requestMatchers(HttpMethod.POST, "/api/auth/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/actuator/health", "/actuator/health/**",
                        "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                .requestMatchers("/actuator/**").denyAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/artist/**").hasRole("ARTIST")
                .requestMatchers("/api/artist-applications/**").authenticated()
                .requestMatchers(HttpMethod.GET,
                        "/api/songs/**",
                        "/api/albums/**",
                        "/api/artists/**",
                        "/api/playlists/**",
                        "/api/stream/**",
                        "/api/search/**",
                        "/api/recommendations/top-songs",
                        "/api/recommendations/new-releases"
                ).permitAll()

                // ADMIN ONLY
                .requestMatchers(HttpMethod.POST,
                        "/api/songs/**",
                        "/api/albums/**",
                        "/api/artists/**"
                ).hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT,
                        "/api/songs/**",
                        "/api/albums/**",
                        "/api/artists/**"
                ).hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE,
                        "/api/songs/**",
                        "/api/albums/**",
                        "/api/artists/**"
                ).hasRole("ADMIN")

                // AUTHENTICATED
                .requestMatchers(
                        "/api/users/**",
                        "/api/favorites/**",
                        "/api/artist-follows/**",
                        "/api/recently-played/**",
                        "/api/recommendations/personalized"
                ).authenticated()
                .requestMatchers(HttpMethod.POST, "/api/playlists/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/playlists/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/playlists/**").authenticated()

                // Default
                .anyRequest().authenticated()
            )

            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)
            )

            .addFilterBefore(
                jwtFilter,
                UsernamePasswordAuthenticationFilter.class
            )

            .httpBasic(httpBasic -> httpBasic.disable());

        return http.build();
    }
}
