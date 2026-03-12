package com.example.flymusic.controller;

import com.example.flymusic.entity.dto.ListeningReportDTO;
import com.example.flymusic.service.ListeningReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 听歌报告控制器
 * 提供听歌报告的REST API接口
 * 
 * 接口列表：
 * - GET /api/report/listening - 获取当前年份报告
 * - GET /api/report/listening/{year} - 获取指定年份报告
 * - GET /api/report/summary - 获取简要统计
 * 
 * @author fly-music
 * @since 2026-03-12
 */
@RestController
@RequestMapping("/api/report")
public class ListeningReportController {
    
    @Autowired
    private ListeningReportService listeningReportService;
    
    /**
     * 获取当前年份的听歌报告
     * 
     * 请求示例：
     * GET /api/report/listening
     * 
     * @return 听歌报告
     */
    @GetMapping("/listening")
    public ResponseEntity<Map<String, Object>> getListeningReport(
            @RequestHeader(value = "X-User-Id", required = false, defaultValue = "1") Long userId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            ListeningReportDTO report = listeningReportService.getListeningReport(userId);
            response.put("code", 200);
            response.put("data", report);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "获取听歌报告失败: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取指定年份的听歌报告
     * 
     * 请求示例：
     * GET /api/report/listening/2025
     * 
     * @param year 年份
     * @return 听歌报告
     */
    @GetMapping("/listening/{year}")
    public ResponseEntity<Map<String, Object>> getListeningReportByYear(
            @PathVariable Integer year,
            @RequestHeader(value = "X-User-Id", required = false, defaultValue = "1") Long userId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            ListeningReportDTO report = listeningReportService.getListeningReportByYear(userId, year);
            response.put("code", 200);
            response.put("data", report);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "获取听歌报告失败: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取简要统计信息
     * 
     * 请求示例：
     * GET /api/report/summary
     * 
     * @return 简要统计信息
     */
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getSummary(
            @RequestHeader(value = "X-User-Id", required = false, defaultValue = "1") Long userId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            ListeningReportDTO report = listeningReportService.getSummary(userId);
            response.put("code", 200);
            response.put("data", report);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "获取统计信息失败: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
}
