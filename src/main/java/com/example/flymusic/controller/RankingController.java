package com.example.flymusic.controller;

import com.example.flymusic.entity.dto.HotRankingDTO;
import com.example.flymusic.service.RankingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/ranking")
public class RankingController {
    
    @Autowired
    private RankingService rankingService;
    
    @GetMapping("/global")
    public ResponseEntity<Map<String, Object>> getGlobalRanking() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            HotRankingDTO ranking = rankingService.getGlobalRanking();
            response.put("code", 200);
            response.put("data", ranking);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "获取热门榜失败: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Map<String, Object>> getCategoryRanking(@PathVariable Long categoryId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            HotRankingDTO ranking = rankingService.getCategoryRanking(categoryId);
            response.put("code", 200);
            response.put("data", ranking);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "获取分类热门榜失败: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/new")
    public ResponseEntity<Map<String, Object>> getNewSongRanking() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            HotRankingDTO ranking = rankingService.getNewSongRanking();
            response.put("code", 200);
            response.put("data", ranking);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "获取新歌榜失败: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/soaring")
    public ResponseEntity<Map<String, Object>> getSoaringRanking() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            HotRankingDTO ranking = rankingService.getSoaringRanking();
            response.put("code", 200);
            response.put("data", ranking);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "获取飙升榜失败: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refreshRankings() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            rankingService.generateAllRankings();
            response.put("code", 200);
            response.put("message", "榜单刷新成功");
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "榜单刷新失败: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
}
