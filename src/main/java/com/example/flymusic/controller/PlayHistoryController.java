package com.example.flymusic.controller;

import com.example.flymusic.entity.PlayHistory;
import com.example.flymusic.entity.Song;
import com.example.flymusic.entity.User;
import com.example.flymusic.repository.PlayHistoryRepository;
import com.example.flymusic.repository.SongRepository;
import com.example.flymusic.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 播放历史Controller
 * 提供播放历史相关的RESTful API接口
 */
@RestController
@RequestMapping("/api/play-history")
public class PlayHistoryController {

    @Autowired
    private PlayHistoryRepository playHistoryRepository;
    
    @Autowired
    private SongRepository songRepository;
    
    @Autowired
    private UserRepository userRepository;

    /**
     * 获取用户的播放历史
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserPlayHistory(@PathVariable Long userId) {
        List<PlayHistory> histories = playHistoryRepository.findByUserIdOrderByPlayedAtDesc(userId);
        return ResponseEntity.ok(createSuccessResponse("获取成功", histories));
    }

    /**
     * 获取用户最近播放历史
     */
    @GetMapping("/user/{userId}/recent")
    public ResponseEntity<Map<String, Object>> getRecentPlayHistory(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "20") int limit) {
        List<PlayHistory> histories = playHistoryRepository.findRecentPlayHistory(userId, PageRequest.of(0, limit));
        return ResponseEntity.ok(createSuccessResponse("获取成功", histories));
    }

    /**
     * 添加播放历史
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> addPlayHistory(@RequestBody Map<String, Long> data) {
        Long userId = data.get("userId");
        Long songId = data.get("songId");
        Integer duration = data.get("duration") != null ? data.get("duration").intValue() : 0;
        
        Optional<User> userOpt = userRepository.findById(userId);
        Optional<Song> songOpt = songRepository.findById(songId);
        
        if (!userOpt.isPresent() || !songOpt.isPresent()) {
            return ResponseEntity.badRequest().body(createErrorResponse("用户或歌曲不存在"));
        }
        
        PlayHistory history = new PlayHistory();
        history.setUser(userOpt.get());
        history.setSong(songOpt.get());
        history.setPlayedAt(new Date());
        history.setPlayDuration(duration);
        
        PlayHistory saved = playHistoryRepository.save(history);
        return ResponseEntity.status(HttpStatus.CREATED).body(createSuccessResponse("添加成功", saved));
    }

    /**
     * 获取用户播放统计
     */
    @GetMapping("/user/{userId}/stats")
    public ResponseEntity<Map<String, Object>> getUserPlayStats(@PathVariable Long userId) {
        long totalSongs = playHistoryRepository.countDistinctSongsByUserId(userId);
        Long totalDuration = playHistoryRepository.sumPlayDurationByUserId(userId);
        List<Object[]> mostPlayed = playHistoryRepository.findMostPlayedSongsByUserId(userId, PageRequest.of(0, 10));
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalSongs", totalSongs);
        stats.put("totalDuration", totalDuration != null ? totalDuration : 0);
        stats.put("mostPlayed", mostPlayed);
        
        return ResponseEntity.ok(createSuccessResponse("获取成功", stats));
    }

    /**
     * 清空用户播放历史
     */
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> clearUserPlayHistory(@PathVariable Long userId) {
        List<PlayHistory> histories = playHistoryRepository.findByUserIdOrderByPlayedAtDesc(userId);
        playHistoryRepository.deleteAll(histories);
        return ResponseEntity.ok(createSuccessResponse("清空成功", null));
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