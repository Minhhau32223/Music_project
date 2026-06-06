package com.spotify.spotify_backend.domain.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Album {
    private Long id;

    private String name;

    private String image;

    private String description;

    private String bgColor;
}
