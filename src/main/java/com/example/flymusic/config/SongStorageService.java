package com.example.flymusic.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
public class SongStorageService {

    @Value("${song.storage.path:./songs}")
    private String storagePath;

    @Value("${song.url.prefix:/api/songs/file/}")
    private String urlPrefix;

    private Path songStoragePath;

    @PostConstruct
    public void init() {
        this.songStoragePath = Paths.get(storagePath).toAbsolutePath();
        try {
            if (!Files.exists(songStoragePath)) {
                Files.createDirectories(songStoragePath);
            }
        } catch (IOException e) {
            throw new RuntimeException("无法创建歌曲存储目录", e);
        }
    }

    public String storeSong(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("文件为空");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String filename = UUID.randomUUID().toString() + extension;
        Path targetPath = songStoragePath.resolve(filename);

        file.transferTo(targetPath.toFile());

        return urlPrefix + filename;
    }

    public void deleteSong(String url) throws IOException {
        if (url == null || !url.startsWith(urlPrefix)) {
            return;
        }

        String filename = url.substring(urlPrefix.length());
        Path filePath = songStoragePath.resolve(filename);

        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }
    }

    public Path getSongPath(String filename) {
        return songStoragePath.resolve(filename);
    }

    public boolean songExists(String url) {
        if (url == null || !url.startsWith(urlPrefix)) {
            return false;
        }

        String filename = url.substring(urlPrefix.length());
        Path filePath = songStoragePath.resolve(filename);

        return Files.exists(filePath);
    }
}
