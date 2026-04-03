package com.example.flymusic.controller;

import com.example.flymusic.entity.UserFavorite;
import com.example.flymusic.entity.User;
import com.example.flymusic.entity.Song;
import com.example.flymusic.repository.UserFavoriteRepository;
import com.example.flymusic.repository.UserRepository;
import com.example.flymusic.repository.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    @Autowired
    private UserFavoriteRepository favoriteRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private SongRepository songRepository;

    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserFavorites(@PathVariable Long userId) {
        List<UserFavorite> favorites = favoriteRepository.findByUserId(userId);
        List<Song> songs = new ArrayList<>();
        
        for (UserFavorite fav : favorites) {
            if (fav.getTargetType() == 1) {
                Optional<Song> songOpt = songRepository.findById(fav.getTargetId());
                songOpt.ifPresent(songs::add);
            }
        }
        
        return ResponseEntity.ok(createSuccessResponse("获取成功", songs));
    }

    @GetMapping("/user/{userId}/type/{targetType}")
    public ResponseEntity<Map<String, Object>> getUserFavoritesByType(
            @PathVariable Long userId,
            @PathVariable Integer targetType) {
        List<UserFavorite> favorites = favoriteRepository.findByUserIdAndTargetType(userId, targetType);
        return ResponseEntity.ok(createSuccessResponse("获取成功", favorites));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> addFavorite(@RequestBody Map<String, Object> data) {
        try {
            Long userId = ((Number) data.get("userId")).longValue();
            Integer targetType = (Integer) data.get("targetType");
            Long targetId = ((Number) data.get("targetId")).longValue();
            
            if (favoriteRepository.existsByUserIdAndTargetTypeAndTargetId(userId, targetType, targetId)) {
                return ResponseEntity.badRequest().body(createErrorResponse("已收藏"));
            }
            
            Optional<User> userOpt = userRepository.findById(userId);
            if (!userOpt.isPresent()) {
                return ResponseEntity.badRequest().body(createErrorResponse("用户不存在"));
            }
            
            UserFavorite favorite = new UserFavorite();
            favorite.setUser(userOpt.get());
            favorite.setTargetType(targetType);
            favorite.setTargetId(targetId);
            favorite.setCreatedAt(new java.util.Date());
            
            UserFavorite saved = favoriteRepository.save(favorite);
            return ResponseEntity.status(HttpStatus.CREATED).body(createSuccessResponse("收藏成功", saved));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createErrorResponse("收藏失败: " + e.getMessage()));
        }
    }

    @DeleteMapping("/remove")
    public ResponseEntity<Map<String, Object>> removeFavorite(@RequestBody Map<String, Object> data) {
        try {
            Long userId = ((Number) data.get("userId")).longValue();
            Integer targetType = (Integer) data.get("targetType");
            Long targetId = ((Number) data.get("targetId")).longValue();
            
            Optional<UserFavorite> favorite = favoriteRepository.findByUserIdAndTargetTypeAndTargetId(userId, targetType, targetId);
            if (favorite.isPresent()) {
                favoriteRepository.delete(favorite.get());
                return ResponseEntity.ok(createSuccessResponse("取消收藏成功", null));
            }
            return ResponseEntity.badRequest().body(createErrorResponse("收藏不存在"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createErrorResponse("取消收藏失败: " + e.getMessage()));
        }
    }

    @GetMapping("/check")
    public ResponseEntity<Map<String, Object>> checkFavorite(
            @RequestParam Long userId,
            @RequestParam Integer targetType,
            @RequestParam Long targetId) {
        boolean exists = favoriteRepository.existsByUserIdAndTargetTypeAndTargetId(userId, targetType, targetId);
        return ResponseEntity.ok(createSuccessResponse("检查成功", exists));
    }
    
    @GetMapping("/check-batch")
    public ResponseEntity<Map<String, Object>> checkBatchFavorite(
            @RequestParam Long userId,
            @RequestParam Integer targetType,
            @RequestParam String songIds) {
        Map<Long, Boolean> result = new HashMap<>();
        String[] ids = songIds.split(",");
        for (String idStr : ids) {
            try {
                Long targetId = Long.parseLong(idStr.trim());
                boolean exists = favoriteRepository.existsByUserIdAndTargetTypeAndTargetId(userId, targetType, targetId);
                result.put(targetId, exists);
            } catch (NumberFormatException e) {
                // skip invalid id
            }
        }
        return ResponseEntity.ok(createSuccessResponse("检查成功", result));
    }

    @GetMapping("/user/{userId}/count")
    public ResponseEntity<Map<String, Object>> countUserFavorites(@PathVariable Long userId) {
        long count = favoriteRepository.countByUserId(userId);
        return ResponseEntity.ok(createSuccessResponse("获取成功", count));
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
