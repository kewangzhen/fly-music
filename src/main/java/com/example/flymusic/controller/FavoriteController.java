package com.example.flymusic.controller;

import com.example.flymusic.entity.UserFavorite;
import com.example.flymusic.repository.UserFavoriteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 用户收藏Controller
 * 提供用户收藏相关的RESTful API接口
 */
@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    @Autowired
    private UserFavoriteRepository favoriteRepository;

    /**
     * 获取用户收藏列表
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserFavorites(@PathVariable Long userId) {
        List<UserFavorite> favorites = favoriteRepository.findByUserId(userId);
        return ResponseEntity.ok(createSuccessResponse("获取成功", favorites));
    }

    /**
     * 获取用户特定类型收藏
     */
    @GetMapping("/user/{userId}/type/{targetType}")
    public ResponseEntity<Map<String, Object>> getUserFavoritesByType(
            @PathVariable Long userId,
            @PathVariable Integer targetType) {
        List<UserFavorite> favorites = favoriteRepository.findByUserIdAndTargetType(userId, targetType);
        return ResponseEntity.ok(createSuccessResponse("获取成功", favorites));
    }

    /**
     * 添加收藏
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> addFavorite(@RequestBody Map<String, Object> data) {
        Long userId = ((Number) data.get("userId")).longValue();
        Integer targetType = (Integer) data.get("targetType");
        Long targetId = ((Number) data.get("targetId")).longValue();
        
        if (favoriteRepository.existsByUserIdAndTargetTypeAndTargetId(userId, targetType, targetId)) {
            return ResponseEntity.badRequest().body(createErrorResponse("已收藏"));
        }
        
        UserFavorite favorite = new UserFavorite();
        // 设置用户和目标...
        favorite.setCreatedAt(new java.util.Date());
        
        UserFavorite saved = favoriteRepository.save(favorite);
        return ResponseEntity.status(HttpStatus.CREATED).body(createSuccessResponse("收藏成功", saved));
    }

    /**
     * 取消收藏
     */
    @DeleteMapping
    public ResponseEntity<Map<String, Object>> removeFavorite(@RequestBody Map<String, Object> data) {
        Long userId = ((Number) data.get("userId")).longValue();
        Integer targetType = (Integer) data.get("targetType");
        Long targetId = ((Number) data.get("targetId")).longValue();
        
        Optional<UserFavorite> favorite = favoriteRepository.findByUserIdAndTargetTypeAndTargetId(userId, targetType, targetId);
        if (favorite.isPresent()) {
            favoriteRepository.delete(favorite.get());
            return ResponseEntity.ok(createSuccessResponse("取消收藏成功", null));
        }
        return ResponseEntity.badRequest().body(createErrorResponse("收藏不存在"));
    }

    /**
     * 检查是否已收藏
     */
    @GetMapping("/check")
    public ResponseEntity<Map<String, Object>> checkFavorite(
            @RequestParam Long userId,
            @RequestParam Integer targetType,
            @RequestParam Long targetId) {
        boolean exists = favoriteRepository.existsByUserIdAndTargetTypeAndTargetId(userId, targetType, targetId);
        return ResponseEntity.ok(createSuccessResponse("检查成功", exists));
    }

    /**
     * 统计用户收藏数量
     */
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