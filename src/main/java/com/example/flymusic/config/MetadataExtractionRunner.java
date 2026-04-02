package com.example.flymusic.config;

import com.example.flymusic.entity.Album;
import com.example.flymusic.entity.Artist;
import com.example.flymusic.entity.Song;
import com.example.flymusic.repository.AlbumRepository;
import com.example.flymusic.repository.ArtistRepository;
import com.example.flymusic.repository.SongRepository;
import com.example.flymusic.service.Mp3MetadataService;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component
@ConditionalOnProperty(name = "flymusic.metadata-extraction.enabled", havingValue = "true", matchIfMissing = false)
public class MetadataExtractionRunner implements CommandLineRunner {

    @Autowired
    private Mp3MetadataService mp3MetadataService;

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private EntityManager entityManager;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        System.out.println("Starting metadata extraction from MP3 files...");
        
        List<Song> songs = songRepository.findAll();
        int updatedCount = 0;
        
        Map<String, Artist> artistCache = new HashMap<>();
        Map<String, Album> albumCache = new HashMap<>();
        
        for (Song song : songs) {
            String url = song.getUrl();
            if (url == null || url.isEmpty()) continue;
            
            String filename = url.substring(url.lastIndexOf('/') + 1);
            Mp3MetadataService.Mp3Metadata metadata = mp3MetadataService.extractMetadata(filename);
            
            boolean updated = false;
            
            if (metadata.getTitle() != null && !metadata.getTitle().isEmpty()) {
                song.setTitle(metadata.getTitle());
                updated = true;
            }
            
            if (metadata.getDuration() != null && metadata.getDuration() > 0) {
                song.setDuration(metadata.getDuration());
                updated = true;
            }
            
            if (metadata.getCoverUrl() != null) {
                song.setCover(metadata.getCoverUrl());
                updated = true;
            }
            
            if (metadata.getLyrics() != null && !metadata.getLyrics().isEmpty()) {
                song.setLyrics(metadata.getLyrics());
                updated = true;
            }
            
            if (metadata.getArtist() != null && !metadata.getArtist().isEmpty()) {
                String artistName = metadata.getArtist();
                Artist artist = artistCache.get(artistName);
                if (artist == null) {
                    artist = artistRepository.findAll().stream()
                        .filter(a -> a.getName().equals(artistName))
                        .findFirst()
                        .orElse(null);
                    if (artist == null) {
                        artist = new Artist();
                        artist.setName(artistName);
                        artist.setStatus(1);
                        artist = artistRepository.save(artist);
                    }
                    artistCache.put(artistName, artist);
                }
                song.setArtists(new java.util.HashSet<>(Set.of(artist)));
                updated = true;
            }
            
            if (metadata.getAlbum() != null && !metadata.getAlbum().isEmpty()) {
                String albumName = metadata.getAlbum();
                Album album = albumCache.get(albumName);
                if (album == null) {
                    album = albumRepository.findAll().stream()
                        .filter(a -> a.getTitle().equals(albumName))
                        .findFirst()
                        .orElse(null);
                    if (album == null) {
                        album = new Album();
                        album.setTitle(albumName);
                        album.setStatus(1);
                        album = albumRepository.save(album);
                    }
                    albumCache.put(albumName, album);
                }
                song.setAlbum(album);
                updated = true;
            }
            
            if (updated) {
                songRepository.save(song);
                updatedCount++;
                if (updatedCount % 50 == 0) {
                    entityManager.flush();
                    entityManager.clear();
                    System.out.println("Updated " + updatedCount + " songs...");
                }
            }
        }
        
        entityManager.flush();
        entityManager.clear();
        System.out.println("Metadata extraction completed! Updated " + updatedCount + " songs");
        System.out.println("Created " + artistCache.size() + " artists");
        System.out.println("Created " + albumCache.size() + " albums");
    }
}
