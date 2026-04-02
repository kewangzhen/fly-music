package com.example.flymusic.service;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
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
public class Mp3MetadataService {

    @Value("${song.storage.path:./songs}")
    private String songsPath;

    @Value("${cover.storage.path:./covers}")
    private String coversPath;

    public Mp3Metadata extractMetadata(String mp3Filename) {
        Mp3Metadata metadata = new Mp3Metadata();
        metadata.setFilename(mp3Filename);
        
        try {
            File songsDir = new File(songsPath);
            if (!songsDir.exists()) {
                songsDir = new File(".");
            }

            File mp3File = new File(songsDir, mp3Filename);
            if (!mp3File.exists()) {
                System.out.println("MP3 file not found: " + mp3File.getAbsolutePath());
                return metadata;
            }

            AudioFile audioFile = AudioFileIO.read(mp3File);
            Tag tag = audioFile.getTagAndConvertOrCreateAndSetDefault();
            
            metadata.setTitle(tag.getFirst(FieldKey.TITLE));
            metadata.setArtist(tag.getFirst(FieldKey.ARTIST));
            metadata.setAlbum(tag.getFirst(FieldKey.ALBUM));
            metadata.setGenre(tag.getFirst(FieldKey.GENRE));
            
            int durationSeconds = audioFile.getAudioHeader().getTrackLength();
            metadata.setDuration(durationSeconds);
            
            metadata.setYear(tag.getFirst(FieldKey.YEAR));
            metadata.setTrack(tag.getFirst(FieldKey.TRACK));
            metadata.setComment(tag.getFirst(FieldKey.COMMENT));
            
            if (tag.getFirst(FieldKey.LYRICS) != null) {
                metadata.setLyrics(tag.getFirst(FieldKey.LYRICS));
            }
            
            if (tag.getFirstArtwork() != null) {
                String coverUrl = extractCover(tag.getFirstArtwork(), mp3Filename);
                metadata.setCoverUrl(coverUrl);
            }
            
        } catch (CannotReadException | TagException | ReadOnlyFileException | InvalidAudioFrameException | IOException e) {
            System.err.println("Error extracting metadata from " + mp3Filename + ": " + e.getMessage());
        }
        
        return metadata;
    }

    private String extractCover(Artwork artwork, String mp3Filename) {
        if (artwork == null) return null;
        
        byte[] imageData = artwork.getBinaryData();
        if (imageData == null || imageData.length == 0) return null;

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

        String coverFilename = sanitizeFilename(mp3Filename) + "." + extension;
        File coverFile = new File(coversDir, coverFilename);

        try (FileOutputStream fos = new FileOutputStream(coverFile)) {
            fos.write(imageData);
        } catch (IOException e) {
            System.err.println("Error saving cover: " + e.getMessage());
            return null;
        }

        return "/api/covers/" + coverFilename;
    }

    private String sanitizeFilename(String filename) {
        return filename.replaceAll("[^a-zA-Z0-9\\-_]", "_").replaceAll("_+", "_");
    }

    public static class Mp3Metadata {
        private String filename;
        private String title;
        private String artist;
        private String album;
        private String genre;
        private Integer duration;
        private String year;
        private String track;
        private String comment;
        private String lyrics;
        private String coverUrl;

        public String getFilename() { return filename; }
        public void setFilename(String filename) { this.filename = filename; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getArtist() { return artist; }
        public void setArtist(String artist) { this.artist = artist; }
        public String getAlbum() { return album; }
        public void setAlbum(String album) { this.album = album; }
        public String getGenre() { return genre; }
        public void setGenre(String genre) { this.genre = genre; }
        public Integer getDuration() { return duration; }
        public void setDuration(Integer duration) { this.duration = duration; }
        public String getYear() { return year; }
        public void setYear(String year) { this.year = year; }
        public String getTrack() { return track; }
        public void setTrack(String track) { this.track = track; }
        public String getComment() { return comment; }
        public void setComment(String comment) { this.comment = comment; }
        public String getLyrics() { return lyrics; }
        public void setLyrics(String lyrics) { this.lyrics = lyrics; }
        public String getCoverUrl() { return coverUrl; }
        public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }
    }
}
