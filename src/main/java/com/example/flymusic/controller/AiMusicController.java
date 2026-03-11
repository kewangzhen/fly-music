package com.example.flymusic.controller;

import com.example.flymusic.entity.AiMusicGeneration;
import com.example.flymusic.repository.AiMusicGenerationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * AI音乐实验室Controller
 * 提供AI音乐生成、听歌识别等RESTful API接口
 */
@RestController
@RequestMapping("/api/ai")
public class AiMusicController {

    @Autowired
    private AiMusicGenerationRepository aiMusicRepository;

    /**
     * AI文本生成音乐
     */
    @PostMapping("/generate")
    public ResponseEntity<Map<String, Object>> generateMusic(@RequestBody Map<String, Object> data) {
        Map<String, Object> result = new HashMap<>();
        result.put("id", 1L);
        result.put("status", "processing");
        result.put("message", "AI音乐生成任务已提交");
        
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(createSuccessResponse("生成中", result));
    }

    /**
     * 获取生成状态
     */
    @GetMapping("/generate/{id}")
    public ResponseEntity<Map<String, Object>> getGenerationStatus(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("status", "completed");
        result.put("musicUrl", "http://example.com/music.mp3");
        
        return ResponseEntity.ok(createSuccessResponse("获取成功", result));
    }

    /**
     * 获取用户AI生成历史
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserGenerations(@PathVariable Long userId) {
        List<AiMusicGeneration> generations = aiMusicRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return ResponseEntity.ok(createSuccessResponse("获取成功", generations));
    }

    /**
     * AI听歌识别曲风
     */
    @PostMapping("/recognize")
    public ResponseEntity<Map<String, Object>> recognizeMusicStyle(@RequestBody Map<String, Object> data) {
        Map<String, Object> result = new HashMap<>();
        result.put("genre", "流行");
        result.put("mood", "欢快");
        result.put("bpm", 120);
        result.put("instruments", Arrays.asList("吉他", "钢琴", "鼓"));
        
        return ResponseEntity.ok(createSuccessResponse("识别成功", result));
    }

    /**
     * 删除AI生成记录
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteGeneration(@PathVariable Long id) {
        aiMusicRepository.deleteById(id);
        return ResponseEntity.ok(createSuccessResponse("删除成功", null));
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