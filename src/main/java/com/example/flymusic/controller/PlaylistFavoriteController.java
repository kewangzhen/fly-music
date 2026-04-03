package com.example.flymusic.controller;

import com.example.flymusic.entity.Playlist;
import com.example.flymusic.entity.PlaylistFavorite;
import com.example.flymusic.entity.User;
import com.example.flymusic.repository.PlaylistFavoriteRepository;
import com.example.flymusic.repository.PlaylistRepository;
import com.example.flymusic.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/playlists")
public class PlaylistFavoriteController {

    @Autowired
    private PlaylistFavoriteRepository playlistFavoriteRepository;
    
    @Autowired
    private PlaylistRepository playlistRepository;
    
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/{playlistId}/favorite")
    public ResponseEntity<Map<String, Object>> favoritePlaylist(
            @PathVariable Long playlistId,
            @RequestBody Map<String, Long> data) {
        Long userId = data.get("userId");
        
        if (playlistFavoriteRepository.existsByUserIdAndPlaylistId(userId, playlistId)) {
            return ResponseEntity.badRequest().body(createErrorResponse("已收藏"));
        }
        
        User user = userRepository.findById(userId).orElse(null);
        Playlist playlist = playlistRepository.findById(playlistId).orElse(null);
        
        if (user == null || playlist == null) {
            return ResponseEntity.badRequest().body(createErrorResponse("用户或歌单不存在"));
        }
        
        PlaylistFavorite favorite = new PlaylistFavorite();
        favorite.setUser(user);
        favorite.setPlaylist(playlist);
        playlistFavoriteRepository.save(favorite);
        
        return ResponseEntity.ok(createSuccessResponse("收藏成功", null));
    }

    @DeleteMapping("/{playlistId}/favorite")
    public ResponseEntity<Map<String, Object>> unfavoritePlaylist(
            @PathVariable Long playlistId,
            @RequestBody Map<String, Long> data) {
        Long userId = data.get("userId");
        
        playlistFavoriteRepository.deleteByUserIdAndPlaylistId(userId, playlistId);
        return ResponseEntity.ok(createSuccessResponse("取消收藏成功", null));
    }

    @GetMapping("/favorite/{userId}")
    public ResponseEntity<Map<String, Object>> getFavoritePlaylists(@PathVariable Long userId) {
        List<PlaylistFavorite> favorites = playlistFavoriteRepository.findByUserId(userId);
        List<Map<String, Object>> playlists = new ArrayList<>();
        
        for (PlaylistFavorite f : favorites) {
            Playlist p = f.getPlaylist();
            Map<String, Object> playlist = new HashMap<>();
            playlist.put("id", p.getId());
            playlist.put("name", p.getName());
            playlist.put("cover", p.getCover());
            playlist.put("description", p.getDescription());
            playlist.put("songCount", p.getSongCount());
            playlist.put("playCount", p.getPlayCount());
            playlist.put("createdAt", f.getCreatedAt());
            playlists.add(playlist);
        }
        
        return ResponseEntity.ok(createSuccessResponse("获取成功", playlists));
    }

    @GetMapping("/{playlistId}/favorite/check")
    public ResponseEntity<Map<String, Object>> checkFavorite(
            @PathVariable Long playlistId,
            @RequestParam Long userId) {
        boolean exists = playlistFavoriteRepository.existsByUserIdAndPlaylistId(userId, playlistId);
        Map<String, Object> result = new HashMap<>();
        result.put("favorited", exists);
        return ResponseEntity.ok(createSuccessResponse("检查成功", result));
    }

    @GetMapping("/{playlistId}/favorite/count")
    public ResponseEntity<Map<String, Object>> countFavorites(@PathVariable Long playlistId) {
        long count = playlistFavoriteRepository.countByPlaylistId(playlistId);
        Map<String, Object> result = new HashMap<>();
        result.put("count", count);
        return ResponseEntity.ok(createSuccessResponse("获取成功", result));
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
