package com.spotify.spotify_backend.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.spotify.spotify_backend.presentation.middleware.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**")
                                    .permitAll()
                .requestMatchers("/api/artist-follows/**",
                                    "/api/search",
                                    "/api/stream/**",
                                    "/api/favorites/**"


                )
                                    .permitAll()
                .requestMatchers(HttpMethod.GET,"/api/songs/**",
                                    "/api/albums/**",
                                    "/api/artists/**",
                                    "/api/playlists/**",
                                    "/api/stream/**",
                                    "/api/search",
                                    "/api/artist-follows/**"
                                    )
                                    .permitAll()
                                    
                .requestMatchers(HttpMethod.POST,"/api/songs/**",
                                    "/api/albums/**",
                                    "/api/artists/**",
                                    "/api/playlists/**",
                                    "/api/stream/**",
                                    "/api/search"
                                    
                                    )
                                    .hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE,"/api/songs/**",
                                    "/api/albums/**",
                                    "/api/artists/**",
                                    "/api/playlists/**",
                                    "/api/stream/**"
                                    )
                                    .hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT,"/api/songs/**",
                                    "/api/albums/**",
                                    "/api/artists/**",
                                    "/api/playlists/**"
                                    
                                    )
                                    .hasRole("ADMIN")   
                .requestMatchers("/api/users/**")
                .authenticated()
                .anyRequest()
                .authenticated()                 
                
                
            )

            .addFilterBefore(
                jwtFilter,
                UsernamePasswordAuthenticationFilter.class
            )

            .httpBasic(httpBasic -> httpBasic.disable());

        return http.build();
    }
}