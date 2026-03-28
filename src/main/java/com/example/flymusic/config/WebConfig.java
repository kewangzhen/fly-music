package com.example.flymusic.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${song.storage.path:./songs}")
    private String songStoragePath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String absolutePath = new java.io.File(songStoragePath).getAbsolutePath();

        registry.addResourceHandler("/api/songs/file/**")
                .addResourceLocations("file:" + absolutePath + "/");
    }
}
