package com.example.flymusic.controller;

import com.example.flymusic.entity.Song;
import com.example.flymusic.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/songs")
public class HeartbeatController {

    @Autowired
    private SongService songService;

    @GetMapping("/heartbeat")
    public ResponseEntity<Map<String, Object>> getHeartbeatSongs(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            List<Song> allSongs = songService.getAllSongs();
            Collections.shuffle(allSongs);
            List<Song> heartbeatSongs = allSongs.stream().limit(12).toList();
            return ResponseEntity.ok(createSuccessResponse("获取成功", heartbeatSongs));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(createErrorResponse("获取失败: " + e.getMessage()));
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
        response.put("code", 500);
        response.put("message", message);
        return response;
    }
}
