package com.example.flymusic.controller;

import com.example.flymusic.service.MurekaApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/music")
public class MusicRecognitionController {

    @Autowired
    private MurekaApiService murekaApiService;

    @PostMapping("/recognize/genre")
    public ResponseEntity<Map<String, Object>> recognizeGenre(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("请选择要识别的音频文件"));
            }

            String filename = file.getOriginalFilename();
            if (filename != null && !filename.toLowerCase().matches(".*\\.(mp3|wav|flac|m4a)$")) {
                return ResponseEntity.badRequest().body(createErrorResponse("不支持的音频格式，请上传 mp3、wav、flac 或 m4a 文件"));
            }

            Map<String, Object> result = murekaApiService.recognizeMusic(file);

            if (Boolean.TRUE.equals(result.get("success"))) {
                return ResponseEntity.ok(createSuccessResponse("识别成功", result));
            } else {
                String error = (String) result.get("error");
                return ResponseEntity.badRequest().body(createErrorResponse(error != null ? error : "识别失败"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse("识别失败: " + e.getMessage()));
        }
    }

    @PostMapping("/recognize/analyze")
    public ResponseEntity<Map<String, Object>> analyzeMusic(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("请选择要分析的音频文件"));
            }

            Map<String, Object> result = murekaApiService.analyzeMusic(file);

            if (Boolean.TRUE.equals(result.get("success"))) {
                return ResponseEntity.ok(createSuccessResponse("分析成功", result));
            } else {
                String error = (String) result.get("error");
                return ResponseEntity.badRequest().body(createErrorResponse(error != null ? error : "分析失败"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse("分析失败: " + e.getMessage()));
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
