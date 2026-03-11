package com.example.flymusic.controller;

import com.example.flymusic.entity.Artist;
import com.example.flymusic.entity.Song;
import com.example.flymusic.repository.ArtistRepository;
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
 * 艺术家Controller
 * 提供艺术家相关的RESTful API接口
 */
@RestController
@RequestMapping("/api/artists")
public class ArtistController {

    @Autowired
    private ArtistRepository artistRepository;
    
    @Autowired
    private SongRepository songRepository;

    /**
     * 获取所有艺术家
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllArtists() {
        List<Artist> artists = artistRepository.findAll();
        return ResponseEntity.ok(createSuccessResponse("获取成功", artists));
    }

    /**
     * 根据ID获取艺术家
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getArtistById(@PathVariable Long id) {
        Optional<Artist> artist = artistRepository.findById(id);
        if (artist.isPresent()) {
            return ResponseEntity.ok(createSuccessResponse("获取成功", artist.get()));
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * 创建艺术家
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createArtist(@RequestBody Artist artist) {
        Artist savedArtist = artistRepository.save(artist);
        return ResponseEntity.status(HttpStatus.CREATED).body(createSuccessResponse("创建成功", savedArtist));
    }

    /**
     * 更新艺术家
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateArtist(@PathVariable Long id, @RequestBody Artist artist) {
        if (!artistRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        artist.setId(id);
        Artist updatedArtist = artistRepository.save(artist);
        return ResponseEntity.ok(createSuccessResponse("更新成功", updatedArtist));
    }

    /**
     * 删除艺术家
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteArtist(@PathVariable Long id) {
        artistRepository.deleteById(id);
        return ResponseEntity.ok(createSuccessResponse("删除成功", null));
    }

    /**
     * 搜索艺术家
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchArtists(@RequestParam String keyword) {
        List<Artist> artists = artistRepository.findByNameContaining(keyword);
        return ResponseEntity.ok(createSuccessResponse("搜索成功", artists));
    }

    /**
     * 获取艺术家的歌曲
     */
    @GetMapping("/{artistId}/songs")
    public ResponseEntity<Map<String, Object>> getArtistSongs(@PathVariable Long artistId) {
        List<Song> songs = songRepository.findByArtistId(artistId);
        return ResponseEntity.ok(createSuccessResponse("获取成功", songs));
    }

    /**
     * 辅助方法：创建成功响应
     */
    private Map<String, Object> createSuccessResponse(String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", message);
        response.put("data", data);
        return response;
    }

    /**
     * 辅助方法：创建错误响应
     */
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 400);
        response.put("message", message);
        return response;
    }
}