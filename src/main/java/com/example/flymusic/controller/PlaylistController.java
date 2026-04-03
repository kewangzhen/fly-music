package com.example.flymusic.controller;

import com.example.flymusic.entity.Playlist;
import com.example.flymusic.entity.Song;
import com.example.flymusic.entity.User;
import com.example.flymusic.repository.UserRepository;
import com.example.flymusic.service.PlaylistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 播放列表Controller
 * 提供播放列表相关的RESTful API接口
 */
@RestController
@RequestMapping("/api/playlists")
public class PlaylistController {

    @Autowired
    private PlaylistService playlistService;
    
    @Autowired
    private UserRepository userRepository;

    /**
     * 获取所有播放列表
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllPlaylists() {
        List<Playlist> playlists = playlistService.getAllPlaylists();
        return ResponseEntity.ok(createSuccessResponse("获取成功", playlists));
    }

    /**
     * 根据ID获取播放列表
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getPlaylistById(@PathVariable Long id) {
        Playlist playlist = playlistService.getPlaylistById(id);
        if (playlist != null) {
            return ResponseEntity.ok(createSuccessResponse("获取成功", playlist));
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * 创建播放列表
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createPlaylist(@RequestBody Map<String, Object> data) {
        try {
            Playlist playlist = new Playlist();
            if (data.get("name") != null) {
                playlist.setName(data.get("name").toString());
            }
            if (data.get("description") != null) {
                playlist.setDescription(data.get("description").toString());
            }
            if (data.get("cover") != null) {
                playlist.setCover(data.get("cover").toString());
            }
            if (data.get("userId") != null) {
                Long userId = Long.valueOf(data.get("userId").toString());
                Optional<User> userOpt = userRepository.findById(userId);
                if (userOpt.isPresent()) {
                    playlist.setUser(userOpt.get());
                }
            }
            
            Playlist createdPlaylist = playlistService.createPlaylist(playlist);
            return ResponseEntity.status(HttpStatus.CREATED).body(createSuccessResponse("创建成功", createdPlaylist));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * 更新播放列表
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updatePlaylist(@PathVariable Long id, @RequestBody Playlist playlist) {
        Playlist updatedPlaylist = playlistService.updatePlaylist(id, playlist);
        if (updatedPlaylist != null) {
            return ResponseEntity.ok(createSuccessResponse("更新成功", updatedPlaylist));
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * 删除播放列表
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deletePlaylist(@PathVariable Long id) {
        playlistService.deletePlaylist(id);
        return ResponseEntity.ok(createSuccessResponse("删除成功", null));
    }

    /**
     * 根据用户ID获取播放列表
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getPlaylistsByUserId(@PathVariable Long userId) {
        List<Playlist> playlists = playlistService.getPlaylistsByUserId(userId);
        return ResponseEntity.ok(createSuccessResponse("获取成功", playlists));
    }

    /**
     * 向播放列表添加歌曲
     */
    @PostMapping("/{playlistId}/songs/{songId}")
    public ResponseEntity<Map<String, Object>> addSongToPlaylist(
            @PathVariable Long playlistId,
            @PathVariable Long songId) {
        playlistService.addSongToPlaylist(playlistId, songId);
        return ResponseEntity.ok(createSuccessResponse("添加成功", null));
    }

    /**
     * 从播放列表移除歌曲
     */
    @DeleteMapping("/{playlistId}/songs/{songId}")
    public ResponseEntity<Map<String, Object>> removeSongFromPlaylist(
            @PathVariable Long playlistId,
            @PathVariable Long songId) {
        playlistService.removeSongFromPlaylist(playlistId, songId);
        return ResponseEntity.ok(createSuccessResponse("移除成功", null));
    }

    /**
     * 获取播放列表中的歌曲
     */
    @GetMapping("/{playlistId}/songs")
    public ResponseEntity<Map<String, Object>> getSongsInPlaylist(@PathVariable Long playlistId) {
        List<Song> songs = playlistService.getSongsInPlaylist(playlistId);
        return ResponseEntity.ok(createSuccessResponse("获取成功", songs));
    }

    /**
     * 搜索播放列表
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchPlaylists(@RequestParam String keyword) {
        List<Playlist> playlists = playlistService.searchPlaylists(keyword);
        return ResponseEntity.ok(createSuccessResponse("搜索成功", playlists));
    }

    /**
     * 获取热门播放列表
     */
    @GetMapping("/popular")
    public ResponseEntity<Map<String, Object>> getPopularPlaylists(@RequestParam(defaultValue = "10") int limit) {
        List<Playlist> playlists = playlistService.getPopularPlaylists(limit);
        return ResponseEntity.ok(createSuccessResponse("获取成功", playlists));
    }

    /**
     * 获取心动歌单
     */
    @GetMapping("/heart")
    public ResponseEntity<Map<String, Object>> getHeartPlaylists(@RequestParam(defaultValue = "10") int limit) {
        List<Playlist> playlists = playlistService.getHeartPlaylists(limit);
        return ResponseEntity.ok(createSuccessResponse("获取成功", playlists));
    }

    /**
     * 获取雷达榜单
     */
    @GetMapping("/radar")
    public ResponseEntity<Map<String, Object>> getRadarPlaylists(@RequestParam(defaultValue = "10") int limit) {
        List<Playlist> playlists = playlistService.getRadarPlaylists(limit);
        return ResponseEntity.ok(createSuccessResponse("获取成功", playlists));
    }

    /**
     * 增加播放次数
     */
    @PostMapping("/{id}/play")
    public ResponseEntity<Map<String, Object>> playPlaylist(@PathVariable Long id) {
        playlistService.incrementPlayCount(id);
        return ResponseEntity.ok(createSuccessResponse("播放成功", null));
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