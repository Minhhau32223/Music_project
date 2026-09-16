package com.spotify.spotify_backend.application.dto.Response;

import java.util.List;

public record PageResult<T>(List<T> content, int pageNumber, int pageSize,
                            long totalElements, int totalPages, boolean last) {}
