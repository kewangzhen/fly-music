package com.example.flymusic.controller;

import com.example.flymusic.entity.Song;
import com.example.flymusic.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 歌曲Controller
 * 提供歌曲相关的RESTful API接口
 */
@RestController
@RequestMapping("/api/songs")
public class SongController {

    @Autowired
    private SongService songService;

    /**
     * 获取所有歌曲
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllSongs() {
        List<Song> songs = songService.getAllSongs();
        return ResponseEntity.ok(createSuccessResponse("获取成功", songs));
    }

    /**
     * 根据ID获取歌曲
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getSongById(@PathVariable Long id) {
        Optional<Song> song = songService.getSongById(id);
        if (song.isPresent()) {
            return ResponseEntity.ok(createSuccessResponse("获取成功", song.get()));
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * 创建歌曲
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createSong(@RequestBody Song song) {
        try {
            Song savedSong = songService.saveSong(song);
            return ResponseEntity.status(HttpStatus.CREATED).body(createSuccessResponse("创建成功", savedSong));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * 更新歌曲
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateSong(@PathVariable Long id, @RequestBody Song song) {
        try {
            Song updatedSong = songService.updateSong(id, song);
            return ResponseEntity.ok(createSuccessResponse("更新成功", updatedSong));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * 删除歌曲
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteSong(@PathVariable Long id) {
        songService.deleteSong(id);
        return ResponseEntity.ok(createSuccessResponse("删除成功", null));
    }

    /**
     * 根据分类ID获取歌曲
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Map<String, Object>> getSongsByCategory(@PathVariable Long categoryId) {
        List<Song> songs = songService.getSongsByCategoryId(categoryId);
        return ResponseEntity.ok(createSuccessResponse("获取成功", songs));
    }

    /**
     * 根据艺术家ID获取歌曲
     */
    @GetMapping("/artist/{artistId}")
    public ResponseEntity<Map<String, Object>> getSongsByArtist(@PathVariable Long artistId) {
        List<Song> songs = songService.getSongsByArtistId(artistId);
        return ResponseEntity.ok(createSuccessResponse("获取成功", songs));
    }

    /**
     * 根据专辑ID获取歌曲
     */
    @GetMapping("/album/{albumId}")
    public ResponseEntity<Map<String, Object>> getSongsByAlbum(@PathVariable Long albumId) {
        List<Song> songs = songService.getSongsByAlbumId(albumId);
        return ResponseEntity.ok(createSuccessResponse("获取成功", songs));
    }

    /**
     * 根据用户ID获取原创歌曲
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getOriginalSongs(@PathVariable Long userId) {
        List<Song> songs = songService.getOriginalSongsByUserId(userId);
        return ResponseEntity.ok(createSuccessResponse("获取成功", songs));
    }

    /**
     * 搜索歌曲
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchSongs(@RequestParam String keyword) {
        List<Song> songs = songService.searchSongs(keyword);
        return ResponseEntity.ok(createSuccessResponse("搜索成功", songs));
    }

    /**
     * 获取热门歌曲
     */
    @GetMapping("/popular")
    public ResponseEntity<Map<String, Object>> getPopularSongs(@RequestParam(defaultValue = "10") int limit) {
        List<Song> songs = songService.getPopularSongs(limit);
        return ResponseEntity.ok(createSuccessResponse("获取成功", songs));
    }

    /**
     * 获取最新歌曲
     */
    @GetMapping("/latest")
    public ResponseEntity<Map<String, Object>> getLatestSongs(@RequestParam(defaultValue = "10") int limit) {
        List<Song> songs = songService.getLatestSongs(limit);
        return ResponseEntity.ok(createSuccessResponse("获取成功", songs));
    }

    /**
     * 获取推荐歌曲
     */
    @GetMapping("/recommended/{userId}")
    public ResponseEntity<Map<String, Object>> getRecommendedSongs(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") int limit) {
        List<Song> songs = songService.getRecommendedSongs(userId, limit);
        return ResponseEntity.ok(createSuccessResponse("获取成功", songs));
    }

    /**
     * 增加播放次数
     */
    @PostMapping("/{id}/play")
    public ResponseEntity<Map<String, Object>> playSong(@PathVariable Long id) {
        songService.incrementPlayCount(id);
        return ResponseEntity.ok(createSuccessResponse("播放成功", null));
    }

    /**
     * 审核歌曲（管理员）
     */
    @PostMapping("/{id}/approve")
    public ResponseEntity<Map<String, Object>> approveSong(@PathVariable Long id) {
        songService.approveSong(id);
        return ResponseEntity.ok(createSuccessResponse("审核通过", null));
    }

    /**
     * 下架歌曲（管理员）
     */
    @PostMapping("/{id}/reject")
    public ResponseEntity<Map<String, Object>> rejectSong(@PathVariable Long id) {
        songService.rejectSong(id);
        return ResponseEntity.ok(createSuccessResponse("歌曲已下架", null));
    }

    /**
     * 分页获取歌曲（管理员）
     */
    @GetMapping("/page")
    public ResponseEntity<Map<String, Object>> getSongsByPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "1") Integer status) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Song> songsPage = songService.getSongsByPage(status, pageable);
        
        Map<String, Object> result = new HashMap<>();
        result.put("content", songsPage.getContent());
        result.put("totalElements", songsPage.getTotalElements());
        result.put("totalPages", songsPage.getTotalPages());
        result.put("currentPage", songsPage.getNumber());
        
        return ResponseEntity.ok(createSuccessResponse("获取成功", result));
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