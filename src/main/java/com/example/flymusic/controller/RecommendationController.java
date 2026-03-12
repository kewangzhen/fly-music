package com.example.flymusic.controller;

import com.example.flymusic.entity.dto.RadarPlaylistDTO;
import com.example.flymusic.entity.dto.UserInterestDTO;
import com.example.flymusic.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/recommendation")
public class RecommendationController {
    
    @Autowired
    private RecommendationService recommendationService;
    
    @GetMapping("/radar")
    public ResponseEntity<Map<String, Object>> getRadarPlaylist(
            @RequestHeader(value = "Authorization", required = false) String token) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long userId = extractUserId(token);
            if (userId == null) {
                response.put("code", 401);
                response.put("message", "请先登录");
                return ResponseEntity.ok(response);
            }
            
            RadarPlaylistDTO radar = recommendationService.getRadarPlaylist(userId);
            
            if (radar == null) {
                response.put("code", 200);
                response.put("data", null);
                response.put("message", "暂无推荐，请先听一些歌曲");
            } else {
                response.put("code", 200);
                response.put("data", radar);
            }
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "获取推荐失败: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/interest")
    public ResponseEntity<Map<String, Object>> getUserInterest(
            @RequestHeader(value = "Authorization", required = false) String token) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long userId = extractUserId(token);
            if (userId == null) {
                response.put("code", 401);
                response.put("message", "请先登录");
                return ResponseEntity.ok(response);
            }
            
            UserInterestDTO interest = recommendationService.getUserInterestProfile(userId);
            
            response.put("code", 200);
            response.put("data", interest);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "获取兴趣画像失败: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/behavior")
    public ResponseEntity<Map<String, Object>> recordBehavior(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestBody Map<String, Object> params) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long userId = extractUserId(token);
            if (userId == null) {
                response.put("code", 401);
                response.put("message", "请先登录");
                return ResponseEntity.ok(response);
            }
            
            Long songId = Long.valueOf(params.get("songId").toString());
            Integer behaviorType = Integer.valueOf(params.get("behaviorType").toString());
            
            recommendationService.recordBehavior(userId, songId, behaviorType);
            
            response.put("code", 200);
            response.put("message", "行为记录成功");
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "记录行为失败: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    private Long extractUserId(String token) {
        if (token == null || token.isEmpty()) {
            return null;
        }
        try {
            return 1L;
        } catch (Exception e) {
            return null;
        }
    }
}
