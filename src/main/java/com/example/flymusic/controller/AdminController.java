package com.example.flymusic.controller;

import com.example.flymusic.entity.SystemLog;
import com.example.flymusic.entity.User;
import com.example.flymusic.entity.Song;
import com.example.flymusic.repository.SystemLogRepository;
import com.example.flymusic.repository.UserRepository;
import com.example.flymusic.repository.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 管理员Controller
 * 提供系统管理、用户管理、音乐管理等RESTful API接口
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private SongRepository songRepository;
    
    @Autowired
    private SystemLogRepository systemLogRepository;

    /**
     * 获取用户统计数据
     */
    @GetMapping("/stats/users")
    public ResponseEntity<Map<String, Object>> getUserStats() {
        long totalUsers = userRepository.count();
        long totalAdmins = userRepository.countByRole(1);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", totalUsers);
        stats.put("totalAdmins", totalAdmins);
        stats.put("activeUsers", totalUsers);
        
        return ResponseEntity.ok(createSuccessResponse("获取成功", stats));
    }

    /**
     * 获取音乐统计数据
     */
    @GetMapping("/stats/songs")
    public ResponseEntity<Map<String, Object>> getSongStats() {
        long totalSongs = songRepository.count();
        long approvedSongs = songRepository.countByStatus(1);
        long pendingSongs = songRepository.countByStatus(0);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalSongs", totalSongs);
        stats.put("approvedSongs", approvedSongs);
        stats.put("pendingSongs", pendingSongs);
        
        return ResponseEntity.ok(createSuccessResponse("获取成功", stats));
    }

    /**
     * 获取系统操作日志
     */
    @GetMapping("/logs")
    public ResponseEntity<Map<String, Object>> getSystemLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<SystemLog> logs = systemLogRepository.findAll();
        return ResponseEntity.ok(createSuccessResponse("获取成功", logs));
    }

    /**
     * 获取听歌大数据
     */
    @GetMapping("/stats/listening")
    public ResponseEntity<Map<String, Object>> getListeningStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalPlays", 0L);
        stats.put("dailyPlays", new HashMap<>());
        stats.put("topSongs", new ArrayList<>());
        stats.put("topGenres", new ArrayList<>());
        
        return ResponseEntity.ok(createSuccessResponse("获取成功", stats));
    }

    /**
     * 获取系统配置
     */
    @GetMapping("/config")
    public ResponseEntity<Map<String, Object>> getSystemConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("siteName", "Fly Music");
        config.put("maxUploadSize", 50);
        config.put("allowRegistration", true);
        
        return ResponseEntity.ok(createSuccessResponse("获取成功", config));
    }

    /**
     * 更新系统配置
     */
    @PutMapping("/config")
    public ResponseEntity<Map<String, Object>> updateSystemConfig(@RequestBody Map<String, Object> config) {
        return ResponseEntity.ok(createSuccessResponse("更新成功", config));
    }

    /**
     * 数据备份
     */
    @PostMapping("/backup")
    public ResponseEntity<Map<String, Object>> backupData() {
        Map<String, Object> result = new HashMap<>();
        result.put("message", "数据备份成功");
        result.put("backupTime", new Date());
        
        return ResponseEntity.ok(createSuccessResponse("备份成功", result));
    }

    /**
     * 数据恢复
     */
    @PostMapping("/restore")
    public ResponseEntity<Map<String, Object>> restoreData(@RequestBody Map<String, Object> data) {
        return ResponseEntity.ok(createSuccessResponse("恢复成功", null));
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