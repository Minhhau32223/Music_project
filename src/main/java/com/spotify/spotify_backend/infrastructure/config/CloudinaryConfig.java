package com.spotify.spotify_backend.infrastructure.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dplmtd7dv",
                "api_key", "614993596941897",
                "api_secret", "4bZStpHP9wu4f7JhMMnGetML3V8",
                "secure", true
        ));
    }
}