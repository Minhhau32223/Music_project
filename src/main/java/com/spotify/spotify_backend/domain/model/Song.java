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
public class Song {

    private Long id;

    private String name;

    private String filePath;

    private String imagePath;

    private String lyrics;

    private String duration;

    private Long playCount;

    private Long albumId;

    private Long artistId;
}

