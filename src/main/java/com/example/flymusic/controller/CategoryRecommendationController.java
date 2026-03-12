package com.example.flymusic.controller;

import com.example.flymusic.entity.dto.CategoryRecommendationDTO;
import com.example.flymusic.service.CategoryRecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 分类推荐控制器
 * 提供分类歌曲推荐的REST API接口
 * 
 * 接口列表：
 * - GET /api/category/{categoryId}/songs - 根据分类ID获取歌曲
 * - GET /api/category/tag/{tag}/songs - 根据标签获取歌曲
 * 
 * @author fly-music
 * @since 2026-03-12
 */
@RestController
@RequestMapping("/api/category")
public class CategoryRecommendationController {
    
    @Autowired
    private CategoryRecommendationService categoryService;
    
    /**
     * 根据分类ID获取推荐歌曲
     * 
     * 请求示例：
     * GET /api/category/1/songs?limit=20
     * 
     * @param categoryId 分类ID
     * @param limit 返回数量，默认20
     * @return 分类歌曲列表
     */
    @GetMapping("/{categoryId}/songs")
    public ResponseEntity<Map<String, Object>> getCategorySongs(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "20") int limit) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 调用服务获取分类歌曲
            List<CategoryRecommendationDTO> songs = categoryService.getCategoryRecommendations(categoryId, limit);
            response.put("code", 200);
            response.put("data", songs);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "获取分类歌曲失败: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 根据标签名称获取推荐歌曲
     * 
     * 请求示例：
     * GET /api/category/tag/流行/songs?limit=20
     * 
     * @param tag 标签名称（分类名称）
     * @param limit 返回数量，默认20
     * @return 分类歌曲列表
     */
    @GetMapping("/tag/{tag}/songs")
    public ResponseEntity<Map<String, Object>> getCategorySongsByTag(
            @PathVariable String tag,
            @RequestParam(defaultValue = "20") int limit) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 调用服务根据标签获取歌曲
            List<CategoryRecommendationDTO> songs = categoryService.getCategoryRecommendationsByTag(tag, limit);
            response.put("code", 200);
            response.put("data", songs);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "获取分类歌曲失败: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
}
