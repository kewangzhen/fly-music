package com.example.flymusic.controller;

import com.example.flymusic.entity.AiMusicGeneration;
import com.example.flymusic.service.AiMusicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.concurrent.Future;

@RestController
@RequestMapping("/api/ai")
public class AiMusicController {

    @Autowired
    private AiMusicService aiMusicService;

    @PostMapping("/generate")
    public ResponseEntity<Map<String, Object>> generateMusic(
            @RequestParam("prompt") String prompt,
            @RequestParam(value = "genre", required = false) String genre,
            @RequestParam(value = "mood", required = false) String mood,
            @RequestParam(value = "duration", defaultValue = "60") Integer duration,
            @RequestParam(value = "userId", required = false) Long userId) {

        if (prompt == null || prompt.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(createErrorResponse("请输入音乐描述"));
        }

        if (userId == null) {
            userId = 2L;
        }

        if (genre == null || genre.isEmpty()) {
            genre = "流行";
        }
        if (mood == null || mood.isEmpty()) {
            mood = "欢快";
        }

        try {
            AiMusicGeneration newGeneration = aiMusicService.createGenerationRecord(userId, prompt, genre, mood, duration);
            
            if (newGeneration == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("用户不存在"));
            }
            
            aiMusicService.generateMusicAsync(userId, prompt, genre, mood, duration);

            Map<String, Object> result = new HashMap<>();
            result.put("id", newGeneration.getId());
            result.put("status", 0);
            result.put("statusText", "生成中");
            result.put("message", "AI音乐生成任务已提交，请稍后查询结果");

            return ResponseEntity.status(HttpStatus.ACCEPTED).body(createSuccessResponse("生成中", result));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createErrorResponse("生成失败: " + e.getMessage()));
        }
    }

    @GetMapping("/generate/{id}")
    public ResponseEntity<Map<String, Object>> getGenerationStatus(@PathVariable Long id) {
        AiMusicGeneration generation = aiMusicService.getGenerationStatus(id);

        if (generation == null) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> result = new HashMap<>();
        result.put("id", generation.getId());
        result.put("status", generation.getStatus());
        result.put("prompt", generation.getPrompt());
        result.put("genre", generation.getGenre());
        result.put("mood", generation.getMood());
        result.put("duration", generation.getDuration());
        result.put("musicUrl", generation.getMusicUrl());
        result.put("cover", generation.getCover());
        result.put("errorMessage", generation.getErrorMessage());
        result.put("createdAt", generation.getCreatedAt());

        String statusText;
        switch (generation.getStatus()) {
            case 0:
                statusText = "处理中";
                break;
            case 1:
                statusText = "已完成";
                break;
            case 2:
                statusText = "失败";
                break;
            default:
                statusText = "未知";
        }
        result.put("statusText", statusText);

        return ResponseEntity.ok(createSuccessResponse("获取成功", result));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserGenerations(@PathVariable Long userId) {
        List<AiMusicGeneration> generations = aiMusicService.getUserGenerations(userId);

        List<Map<String, Object>> resultList = new ArrayList<>();
        for (AiMusicGeneration gen : generations) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", gen.getId());
            item.put("prompt", gen.getPrompt());
            item.put("genre", gen.getGenre());
            item.put("mood", gen.getMood());
            item.put("duration", gen.getDuration());
            item.put("musicUrl", gen.getMusicUrl());
            item.put("cover", gen.getCover());
            item.put("status", gen.getStatus());
            item.put("createdAt", gen.getCreatedAt());
            resultList.add(item);
        }

        return ResponseEntity.ok(createSuccessResponse("获取成功", resultList));
    }

    @PostMapping("/recognize")
    public ResponseEntity<Map<String, Object>> recognizeMusicStyle(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(createErrorResponse("请上传音乐文件"));
        }

        try {
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("audio/")) {
                return ResponseEntity.badRequest().body(createErrorResponse("文件必须是音频文件"));
            }

            Map<String, Object> result = aiMusicService.recognizeMusicStyle(file);

            return ResponseEntity.ok(createSuccessResponse("识别成功", result));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createErrorResponse("识别失败: " + e.getMessage()));
        }
    }

    @GetMapping("/models")
    public ResponseEntity<Map<String, Object>> getAvailableModels() {
        Map<String, Object> models = new HashMap<>();
        
        models.put("generation", Arrays.asList(
            Map.of("id", "acestep", "name", "ACE-Step 1.5", "description", "最强开源音乐生成，支持曲风识别"),
            Map.of("id", "musicgen", "name", "MusicGen", "description", "Meta 开源音乐生成模型")
        ));
        
        models.put("recognition", Arrays.asList(
            Map.of("id", "acestep", "name", "ACE-Step", "description", "支持 BPM、调性、风格识别"),
            Map.of("id", "simple", "name", "Simple", "description", "基础音频特征分析")
        ));
        
        return ResponseEntity.ok(createSuccessResponse("获取成功", models));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteGeneration(@PathVariable Long id) {
        try {
            aiMusicService.deleteGeneration(id);
            return ResponseEntity.ok(createSuccessResponse("删除成功", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse("删除失败: " + e.getMessage()));
        }
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
