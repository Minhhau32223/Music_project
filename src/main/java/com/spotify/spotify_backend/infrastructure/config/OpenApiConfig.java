package com.spotify.spotify_backend.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import java.util.List;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI musicApi() {
        var components = new Components().addSecuritySchemes("bearerAuth", new SecurityScheme()
                .type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT"));
        return new OpenAPI().info(new Info().title("Music Backend API").version("v1")
                .description("Public catalog, JWT user actions, and ADMIN catalog mutations. Pagination starts at zero."))
                .components(components);
    }

    @Bean
    public OpenApiCustomizer endpointSecurity() {
        return api -> api.getPaths().forEach((path, item) -> item.readOperationsMap().forEach((method, operation) -> {
            boolean catalog = List.of("/api/songs", "/api/albums", "/api/artists")
                    .stream().anyMatch(path::startsWith);
            boolean publicRead = method.name().equals("GET") && (catalog
                    || path.startsWith("/api/playlists") || path.startsWith("/api/stream")
                    || path.startsWith("/api/search") || path.equals("/api/recommendations/top-songs")
                    || path.equals("/api/recommendations/new-releases"));
            boolean anonymous = publicRead || (method.name().equals("POST") && path.startsWith("/api/auth/"));
            boolean admin = catalog && List.of("POST", "PUT", "DELETE").contains(method.name());
            operation.setSecurity(anonymous ? List.of() : List.of(new SecurityRequirement().addList("bearerAuth")));
            String permission = anonymous ? "PUBLIC" : admin ? "ADMIN" : "Authenticated USER or ADMIN; playlist mutations require ownership";
            operation.setDescription((operation.getDescription() == null ? "" : operation.getDescription() + "\n")
                    + "Permission: " + permission);
            for (String status : anonymous ? List.of("400", "404") : List.of("400", "401", "403", "404")) {
                operation.getResponses().addApiResponse(status, new ApiResponse().description(switch (status) {
                    case "400" -> "Invalid request (validation errors may include an errors map)";
                    case "401" -> "Missing, expired or invalid JWT";
                    case "403" -> "Insufficient role or playlist ownership";
                    default -> "Resource not found";
                }).content(new Content().addMediaType("application/json", new MediaType()
                        .schema(errorSchema()))));
            }
        }));
    }

    private Schema<?> errorSchema() {
        return new ObjectSchema()
                .addProperty("timestamp", new StringSchema().format("date-time"))
                .addProperty("status", new IntegerSchema())
                .addProperty("error", new StringSchema())
                .addProperty("message", new StringSchema())
                .addProperty("path", new StringSchema());
    }
}
