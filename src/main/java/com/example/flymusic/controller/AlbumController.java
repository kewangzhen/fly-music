package com.example.flymusic.controller;

import com.example.flymusic.entity.Album;
import com.example.flymusic.entity.Song;
import com.example.flymusic.repository.AlbumRepository;
import com.example.flymusic.repository.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 专辑Controller
 * 提供专辑相关的RESTful API接口
 */
@RestController
@RequestMapping("/api/albums")
public class AlbumController {

    @Autowired
    private AlbumRepository albumRepository;
    
    @Autowired
    private SongRepository songRepository;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllAlbums() {
        List<Album> albums = albumRepository.findAll();
        return ResponseEntity.ok(createSuccessResponse("获取成功", albums));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getAlbumById(@PathVariable Long id) {
        Optional<Album> album = albumRepository.findById(id);
        if (album.isPresent()) {
            return ResponseEntity.ok(createSuccessResponse("获取成功", album.get()));
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createAlbum(@RequestBody Album album) {
        Album savedAlbum = albumRepository.save(album);
        return ResponseEntity.status(HttpStatus.CREATED).body(createSuccessResponse("创建成功", savedAlbum));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateAlbum(@PathVariable Long id, @RequestBody Album album) {
        if (!albumRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        album.setId(id);
        Album updatedAlbum = albumRepository.save(album);
        return ResponseEntity.ok(createSuccessResponse("更新成功", updatedAlbum));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteAlbum(@PathVariable Long id) {
        albumRepository.deleteById(id);
        return ResponseEntity.ok(createSuccessResponse("删除成功", null));
    }

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchAlbums(@RequestParam String keyword) {
        List<Album> albums = albumRepository.findByTitleContaining(keyword);
        return ResponseEntity.ok(createSuccessResponse("搜索成功", albums));
    }

    @GetMapping("/{albumId}/songs")
    public ResponseEntity<Map<String, Object>> getAlbumSongs(@PathVariable Long albumId) {
        List<Song> songs = songRepository.findByAlbumId(albumId);
        return ResponseEntity.ok(createSuccessResponse("获取成功", songs));
    }

    @GetMapping("/artist/{artistId}")
    public ResponseEntity<Map<String, Object>> getAlbumsByArtist(@PathVariable Long artistId) {
        List<Album> albums = albumRepository.findByArtistId(artistId);
        return ResponseEntity.ok(createSuccessResponse("获取成功", albums));
    }

    private Map<String, Object> createSuccessResponse(String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", message);
        response.put("data", data);
        return response;
    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 400);
        response.put("message", message);
        return response;
    }
}