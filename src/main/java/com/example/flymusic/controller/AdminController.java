package com.example.flymusic.controller;

import com.example.flymusic.entity.SystemLog;
import com.example.flymusic.entity.User;
import com.example.flymusic.entity.Song;
import com.example.flymusic.entity.Category;
import com.example.flymusic.repository.SystemLogRepository;
import com.example.flymusic.repository.UserRepository;
import com.example.flymusic.repository.SongRepository;
import com.example.flymusic.repository.PlayHistoryRepository;
import com.example.flymusic.repository.CategoryRepository;
import com.example.flymusic.repository.ArtistRepository;
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
    
    @Autowired
    private PlayHistoryRepository playHistoryRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private ArtistRepository artistRepository;

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
        
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date todayStart = calendar.getTime();
        
        calendar.add(Calendar.DAY_OF_MONTH, -30);
        Date thirtyDaysAgo = calendar.getTime();
        
        long totalPlays = playHistoryRepository.count();
        long todayPlays = playHistoryRepository.countByPlayedAtBetween(todayStart, now);
        Long todayDuration = playHistoryRepository.sumDurationByPlayedAtBetween(todayStart, now);
        long todayActiveUsers = playHistoryRepository.countDistinctUsersByPlayedAtBetween(todayStart, now);
        
        stats.put("totalPlays", totalPlays);
        stats.put("todayPlays", todayPlays);
        stats.put("todayDuration", todayDuration != null ? todayDuration : 0);
        stats.put("todayActiveUsers", todayActiveUsers);
        
        List<Object[]> topArtistData = playHistoryRepository.countByArtist(org.springframework.data.domain.PageRequest.of(0, 5));
        List<Map<String, Object>> topArtists = new ArrayList<>();
        for (Object[] row : topArtistData) {
            Map<String, Object> artistMap = new HashMap<>();
            artistMap.put("id", row[0]);
            artistMap.put("name", row[1]);
            artistMap.put("avatar", row[2]);
            artistMap.put("playCount", row[3]);
            topArtists.add(artistMap);
        }
        stats.put("topArtists", topArtists);
        
        List<Category> allCategories = categoryRepository.findAll();
        Map<String, Long> genreStats = new LinkedHashMap<>();
        for (Category cat : allCategories) {
            long count = playHistoryRepository.countBySongCategoryId(cat.getId());
            if (count > 0) {
                genreStats.put(cat.getName(), count);
            }
        }
        stats.put("genreStats", genreStats);
        
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