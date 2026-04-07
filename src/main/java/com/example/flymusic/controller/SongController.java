package com.example.flymusic.controller;

import com.example.flymusic.config.SongStorageService;
import com.example.flymusic.entity.Song;
import com.example.flymusic.entity.User;
import com.example.flymusic.repository.UserRepository;
import com.example.flymusic.service.CoverExtractService;
import com.example.flymusic.service.SongService;
import com.example.flymusic.service.SystemLogService;
import com.example.flymusic.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 歌曲Controller
 * 提供歌曲相关的RESTful API接口
 */
@RestController
@RequestMapping("/api/songs")
public class SongController {

    @Autowired
    private SongService songService;

    @Autowired
    private SongStorageService songStorageService;

    @Autowired
    private CoverExtractService coverExtractService;
    
    @Autowired
    private SystemLogService systemLogService;
    
    @Autowired
    private JwtUtils jwtUtils;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private HttpServletRequest request;
    
    private String getIpAddress() {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
    
    private void logAction(String action, String targetType, Long targetId, String details, int status) {
        try {
            String token = request.getHeader("Authorization");
            Long userId = null;
            String username = null;
            if (token != null && token.startsWith("Bearer ")) {
                String jwtToken = token.replace("Bearer ", "");
                userId = jwtUtils.getUserIdFromToken(jwtToken);
                Optional<User> user = userRepository.findById(userId);
                if (user.isPresent()) {
                    username = user.get().getUsername();
                }
            }
            systemLogService.logAction(userId, username, action, targetType, targetId, 
                request.getMethod(), getIpAddress(), request.getHeader("User-Agent"), 
                details, status, null);
        } catch (Exception e) {
        }
    }
    
    @Value("${song.url.prefix:/api/songs/file/}")
    private String songUrlPrefix;

    /**
     * 获取所有歌曲
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllSongs() {
        List<Song> songs = songService.getAllSongs();
        return ResponseEntity.ok(createSuccessResponse("获取成功", songs));
    }

    /**
     * 根据ID获取歌曲
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getSongById(@PathVariable Long id) {
        Optional<Song> song = songService.getSongById(id);
        if (song.isPresent()) {
            return ResponseEntity.ok(createSuccessResponse("获取成功", song.get()));
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * 创建歌曲
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createSong(@RequestBody Song song) {
        try {
            Song savedSong = songService.saveSong(song);
            return ResponseEntity.status(HttpStatus.CREATED).body(createSuccessResponse("创建成功", savedSong));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * 更新歌曲
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateSong(@PathVariable Long id, @RequestBody Song song) {
        try {
            Song updatedSong = songService.updateSong(id, song);
            return ResponseEntity.ok(createSuccessResponse("更新成功", updatedSong));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * 删除歌曲
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteSong(@PathVariable Long id) {
        songService.deleteSong(id);
        return ResponseEntity.ok(createSuccessResponse("删除成功", null));
    }

    /**
     * 根据分类ID获取歌曲（分页）
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Map<String, Object>> getSongsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Song> songsPage = songService.getSongsByCategoryIdPage(categoryId, pageable);
        
        Map<String, Object> result = new HashMap<>();
        result.put("content", songsPage.getContent());
        result.put("totalElements", songsPage.getTotalElements());
        result.put("totalPages", songsPage.getTotalPages());
        
        return ResponseEntity.ok(createSuccessResponse("获取成功", result));
    }

    /**
     * 根据艺术家ID获取歌曲
     */
    @GetMapping("/artist/{artistId}")
    public ResponseEntity<Map<String, Object>> getSongsByArtist(@PathVariable Long artistId) {
        List<Song> songs = songService.getSongsByArtistId(artistId);
        return ResponseEntity.ok(createSuccessResponse("获取成功", songs));
    }

    /**
     * 根据专辑ID获取歌曲
     */
    @GetMapping("/album/{albumId}")
    public ResponseEntity<Map<String, Object>> getSongsByAlbum(@PathVariable Long albumId) {
        List<Song> songs = songService.getSongsByAlbumId(albumId);
        return ResponseEntity.ok(createSuccessResponse("获取成功", songs));
    }

    /**
     * 根据用户ID获取原创歌曲
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getOriginalSongs(@PathVariable Long userId) {
        List<Song> songs = songService.getOriginalSongsByUserId(userId);
        return ResponseEntity.ok(createSuccessResponse("获取成功", songs));
    }

    /**
     * 搜索歌曲
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchSongs(@RequestParam String keyword) {
        List<Song> songs = songService.searchSongs(keyword);
        return ResponseEntity.ok(createSuccessResponse("搜索成功", songs));
    }

    /**
     * 获取热门歌曲
     */
    @GetMapping("/popular")
    public ResponseEntity<Map<String, Object>> getPopularSongs(@RequestParam(defaultValue = "10") int limit) {
        List<Song> songs = songService.getPopularSongs(limit);
        return ResponseEntity.ok(createSuccessResponse("获取成功", songs));
    }

    /**
     * 获取最新歌曲
     */
    @GetMapping("/latest")
    public ResponseEntity<Map<String, Object>> getLatestSongs(@RequestParam(defaultValue = "10") int limit) {
        List<Song> songs = songService.getLatestSongs(limit);
        return ResponseEntity.ok(createSuccessResponse("获取成功", songs));
    }

    /**
     * 获取推荐歌曲
     */
    @GetMapping("/recommended/{userId}")
    public ResponseEntity<Map<String, Object>> getRecommendedSongs(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") int limit) {
        List<Song> songs = songService.getRecommendedSongs(userId, limit);
        return ResponseEntity.ok(createSuccessResponse("获取成功", songs));
    }

    /**
     * 增加播放次数
     */
    @PostMapping("/{id}/play")
    public ResponseEntity<Map<String, Object>> playSong(@PathVariable Long id) {
        songService.incrementPlayCount(id);
        logAction("song_play", "Song", id, "play count incremented", 1);
        return ResponseEntity.ok(createSuccessResponse("播放成功", null));
    }

    /**
     * 审核歌曲（管理员）
     */
    @PostMapping("/{id}/approve")
    public ResponseEntity<Map<String, Object>> approveSong(@PathVariable Long id) {
        songService.approveSong(id);
        logAction("song_approve", "Song", id, "approved", 1);
        return ResponseEntity.ok(createSuccessResponse("审核通过", null));
    }

    /**
     * 下架歌曲（管理员）
     */
    @PostMapping("/{id}/reject")
    public ResponseEntity<Map<String, Object>> rejectSong(@PathVariable Long id) {
        songService.rejectSong(id);
        return ResponseEntity.ok(createSuccessResponse("歌曲已下架", null));
    }

    /**
     * 分页获取歌曲（管理员）
     */
    @GetMapping("/page")
    public ResponseEntity<Map<String, Object>> getSongsByPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "1") Integer status) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Song> songsPage = songService.getSongsByPage(status, pageable);

        Map<String, Object> result = new HashMap<>();
        result.put("content", songsPage.getContent());
        result.put("totalElements", songsPage.getTotalElements());
        result.put("totalPages", songsPage.getTotalPages());
        result.put("currentPage", songsPage.getNumber());

        return ResponseEntity.ok(createSuccessResponse("获取成功", result));
    }

    /**
     * 上传歌曲文件
     */
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadSong(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "title", required = false) String title) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("文件为空"));
            }

            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || !originalFilename.toLowerCase().endsWith(".mp3")) {
                return ResponseEntity.badRequest().body(createErrorResponse("只允许上传 MP3 文件"));
            }

            String contentType = file.getContentType();
            if (contentType == null || (!contentType.startsWith("audio/") && !contentType.equals("application/octet-stream"))) {
                return ResponseEntity.badRequest().body(createErrorResponse("文件类型必须是音频文件"));
            }

            String url = songStorageService.storeSong(file);

            String coverUrl = null;
            try {
                String tempFilename = "upload_" + System.currentTimeMillis() + ".mp3";
                File tempFile = new File(coverExtractService.getCoverPath() + "/temp_" + tempFilename);
                tempFile.getParentFile().mkdirs();
                file.transferTo(tempFile);
                coverUrl = coverExtractService.extractCoverFromMp3(tempFilename, System.currentTimeMillis());
                tempFile.delete();
            } catch (Exception e) {
                System.out.println("提取封面失败: " + e.getMessage());
            }

            Map<String, Object> result = new HashMap<>();
            result.put("url", url);
            result.put("filename", file.getOriginalFilename());
            result.put("size", file.getSize());
            if (coverUrl != null) {
                result.put("cover", coverUrl);
            }

            return ResponseEntity.ok(createSuccessResponse("上传成功", result));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createErrorResponse("上传失败: " + e.getMessage()));
        }
    }

    /**
     * 获取歌曲文件
     */
    @GetMapping("/file/{filename:.+}")
    public ResponseEntity<Resource> getSongFile(@PathVariable String filename) {
        try {
            Path filePath = songStorageService.getSongPath(filename);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                String contentType = determineContentType(filename);
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 删除歌曲文件
     */
    @DeleteMapping("/file")
    public ResponseEntity<Map<String, Object>> deleteSongFile(@RequestParam String url) {
        try {
            songStorageService.deleteSong(url);
            return ResponseEntity.ok(createSuccessResponse("删除成功", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse("删除失败: " + e.getMessage()));
        }
    }

    private String determineContentType(String filename) {
        String lowerFilename = filename.toLowerCase();
        if (lowerFilename.endsWith(".mp3")) {
            return "audio/mpeg";
        } else if (lowerFilename.endsWith(".wav")) {
            return "audio/wav";
        } else if (lowerFilename.endsWith(".flac")) {
            return "audio/flac";
        } else if (lowerFilename.endsWith(".aac")) {
            return "audio/aac";
        } else if (lowerFilename.endsWith(".ogg")) {
            return "audio/ogg";
        } else if (lowerFilename.endsWith(".m4a")) {
            return "audio/mp4";
        }
        return "application/octet-stream";
    }

    /**
     * 辅助方法：创建成功响应
     */
    private Map<String, Object> createSuccessResponse(String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", message);
        response.put("data", data);
        return response;
    }

    /**
     * 辅助方法：创建错误响应
     */
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 400);
        response.put("message", message);
        return response;
    }
}