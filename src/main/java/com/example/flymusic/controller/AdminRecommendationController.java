package com.example.flymusic.controller;

import com.example.flymusic.entity.dto.AdminRecommendationDTO;
import com.example.flymusic.service.AdminRecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/recommendation")
public class AdminRecommendationController {

    @Autowired
    private AdminRecommendationService adminRecommendationService;

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        Map<String, Object> response = new HashMap<>();
        try {
            AdminRecommendationDTO data = adminRecommendationService.getRecommendationData();
            response.put("code", 200);
            response.put("data", data);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "获取推荐数据失败: " + e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/config")
    public ResponseEntity<Map<String, Object>> getConfig() {
        Map<String, Object> response = new HashMap<>();
        try {
            AdminRecommendationDTO.RecommendationConfig config = adminRecommendationService.getConfig();
            response.put("code", 200);
            response.put("data", config);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "获取配置失败: " + e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    @PutMapping("/config")
    public ResponseEntity<Map<String, Object>> updateConfig(@RequestBody AdminRecommendationDTO.RecommendationConfig config) {
        Map<String, Object> response = new HashMap<>();
        try {
            adminRecommendationService.updateConfig(config);
            adminRecommendationService.recalculateWithNewWeights();
            response.put("code", 200);
            response.put("message", "配置更新成功，已使用新权重重新计算推荐");
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "配置更新失败: " + e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/recalculate")
    public ResponseEntity<Map<String, Object>> recalculate() {
        Map<String, Object> response = new HashMap<>();
        try {
            adminRecommendationService.recalculateWithNewWeights();
            response.put("code", 200);
            response.put("message", "重新计算成功");
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "重新计算失败: " + e.getMessage());
        }
        return ResponseEntity.ok(response);
    }
}
