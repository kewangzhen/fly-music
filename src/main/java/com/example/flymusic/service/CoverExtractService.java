package com.example.flymusic.service;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.images.Artwork;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class CoverExtractService {

    @Value("${song.storage.path:./songs}")
    private String songsPath;

    @Value("${cover.storage.path:./covers}")
    private String coversPath;

    public String extractCoverFromMp3(String mp3Filename, Long songId) {
        try {
            File songsDir = new File(songsPath);
            if (!songsDir.exists()) {
                songsDir = new File(".");
            }

            File mp3File = new File(songsDir, mp3Filename);
            if (!mp3File.exists()) {
                System.out.println("MP3 file not found: " + mp3File.getAbsolutePath());
                return null;
            }

            AudioFile audioFile = AudioFileIO.read(mp3File);
            Tag tag = audioFile.getTag();

            if (tag == null) {
                System.out.println("No tag found in: " + mp3Filename);
                return null;
            }

            Artwork artwork = tag.getFirstArtwork();
            if (artwork == null) {
                System.out.println("No cover art found in: " + mp3Filename);
                return null;
            }

            byte[] imageData = artwork.getBinaryData();
            if (imageData == null || imageData.length == 0) {
                return null;
            }

            String extension = "jpg";
            String mimeType = artwork.getMimeType();
            if (mimeType != null) {
                if (mimeType.contains("png")) {
                    extension = "png";
                } else if (mimeType.contains("gif")) {
                    extension = "gif";
                }
            }

            File coversDir = new File(coversPath);
            if (!coversDir.exists()) {
                coversDir.mkdirs();
            }

            String coverFilename = songId + "_" + sanitizeFilename(mp3Filename) + "." + extension;
            File coverFile = new File(coversDir, coverFilename);

            try (FileOutputStream fos = new FileOutputStream(coverFile)) {
                fos.write(imageData);
            }

            String coverUrl = "/api/covers/" + coverFilename;
            System.out.println("Extracted cover for song " + songId + ": " + coverUrl);
            return coverUrl;

        } catch (Exception e) {
            System.err.println("Error extracting cover from " + mp3Filename + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public void extractAllCovers() {
        File songsDir = new File(songsPath);
        if (!songsDir.exists()) {
            System.out.println("Songs directory does not exist: " + songsPath);
            return;
        }

        File[] mp3Files = songsDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".mp3"));
        if (mp3Files == null || mp3Files.length == 0) {
            System.out.println("No MP3 files found in: " + songsPath);
            return;
        }

        System.out.println("Found " + mp3Files.length + " MP3 files, starting cover extraction...");
        int successCount = 0;
        int failCount = 0;

        for (File mp3File : mp3Files) {
            String filename = mp3File.getName();
            String songIdStr = filename.split("[_\\-\\.]")[0];
            try {
                Long songId = Long.parseLong(songIdStr);
                String cover = extractCoverFromMp3(filename, songId);
                if (cover != null) {
                    successCount++;
                } else {
                    failCount++;
                }
            } catch (Exception e) {
                failCount++;
                System.out.println("Skipping file (cannot parse song ID): " + filename);
            }
        }

        System.out.println("Cover extraction completed! Success: " + successCount + ", Failed: " + failCount);
    }

    private String sanitizeFilename(String filename) {
        return filename.replaceAll("[^a-zA-Z0-9\\-_]", "_");
    }

    public String getCoverPath() {
        return coversPath;
    }

    public Integer getMp3Duration(String mp3FilePath) {
        try {
            File mp3File = new File(mp3FilePath);
            if (!mp3File.exists()) {
                return null;
            }
            AudioFile audioFile = AudioFileIO.read(mp3File);
            if (audioFile != null && audioFile.getAudioHeader() != null) {
                return audioFile.getAudioHeader().getTrackLength();
            }
        } catch (Exception e) {
            System.err.println("Error getting MP3 duration: " + e.getMessage());
        }
        return null;
    }
}