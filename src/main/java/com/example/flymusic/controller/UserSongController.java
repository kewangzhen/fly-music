package com.example.flymusic.controller;

import com.example.flymusic.entity.Song;
import com.example.flymusic.config.SongStorageService;
import com.example.flymusic.service.CoverExtractService;
import com.example.flymusic.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

@RestController
@RequestMapping("/api/user/songs")
public class UserSongController {

    @Autowired
    private SongStorageService songStorageService;

    @Autowired
    private CoverExtractService coverExtractService;

    @Autowired
    private SongService songService;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadUserSong(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") Long userId,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "duration", required = false) Integer duration,
            @RequestParam(value = "cover", required = false) String cover) {
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

            String coverUrl = cover;
            String songTitle = title;
            Integer songDuration = duration;

            try {
                String tempFilename = "user_upload_" + System.currentTimeMillis() + ".mp3";
                File tempFile = new File(coverExtractService.getCoverPath() + "/temp_" + tempFilename);
                tempFile.getParentFile().mkdirs();
                file.transferTo(tempFile);

                if (coverUrl == null || coverUrl.isEmpty()) {
                    coverUrl = coverExtractService.extractCoverFromMp3(tempFilename, System.currentTimeMillis());
                }

                if (songTitle == null || songTitle.isEmpty()) {
                    songTitle = originalFilename.replace(".mp3", "");
                }

                if (songDuration == null) {
                    songDuration = coverExtractService.getMp3Duration(tempFilename);
                }

                tempFile.delete();
            } catch (Exception e) {
                System.out.println("提取歌曲信息失败: " + e.getMessage());
                if (songTitle == null || songTitle.isEmpty()) {
                    songTitle = originalFilename.replace(".mp3", "");
                }
            }

            Song song = new Song();
            song.setTitle(songTitle);
            song.setUrl(url);
            song.setCover(coverUrl);
            song.setDuration(songDuration);
            song.setUserId(userId);
            song.setUserUpload(1);
            song.setIsOriginal(1);
            song.setStatus(1);
            song.setPlayCount(0L);
            song.setDownloadCount(0);
            song.setShareCount(0);

            Song savedSong = songService.saveSong(song);

            return ResponseEntity.status(HttpStatus.CREATED).body(createSuccessResponse("上传成功", savedSong));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createErrorResponse("上传失败: " + e.getMessage()));
        }
    }

    @GetMapping("/my-uploads")
    public ResponseEntity<Map<String, Object>> getMyUploads(@RequestParam Long userId) {
        try {
            List<Song> songs = songService.getOriginalSongsByUserId(userId);
            return ResponseEntity.ok(createSuccessResponse("获取成功", songs));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createErrorResponse("获取失败: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteMyUpload(
            @PathVariable Long id,
            @RequestParam Long userId) {
        try {
            Optional<Song> songOpt = songService.getSongById(id);
            if (!songOpt.isPresent()) {
                return ResponseEntity.badRequest().body(createErrorResponse("歌曲不存在"));
            }
            Song song = songOpt.get();
            if (!song.getUserId().equals(userId)) {
                return ResponseEntity.badRequest().body(createErrorResponse("无权操作"));
            }

            songService.deleteSong(id);
            return ResponseEntity.ok(createSuccessResponse("删除成功", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createErrorResponse("删除失败: " + e.getMessage()));
        }
    }

    @PostMapping("/{id}/offline")
    public ResponseEntity<Map<String, Object>> offlineSong(
            @PathVariable Long id,
            @RequestParam Long userId) {
        try {
            Optional<Song> songOpt = songService.getSongById(id);
            if (!songOpt.isPresent()) {
                return ResponseEntity.badRequest().body(createErrorResponse("歌曲不存在"));
            }
            Song song = songOpt.get();
            if (!song.getUserId().equals(userId)) {
                return ResponseEntity.badRequest().body(createErrorResponse("无权操作"));
            }

            song.setStatus(0);
            songService.saveSong(song);
            return ResponseEntity.ok(createSuccessResponse("已下架", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createErrorResponse("操作失败: " + e.getMessage()));
        }
    }

    @PostMapping("/{id}/online")
    public ResponseEntity<Map<String, Object>> onlineSong(
            @PathVariable Long id,
            @RequestParam Long userId) {
        try {
            Optional<Song> songOpt = songService.getSongById(id);
            if (!songOpt.isPresent()) {
                return ResponseEntity.badRequest().body(createErrorResponse("歌曲不存在"));
            }
            Song song = songOpt.get();
            if (!song.getUserId().equals(userId)) {
                return ResponseEntity.badRequest().body(createErrorResponse("无权操作"));
            }

            song.setStatus(1);
            songService.saveSong(song);
            return ResponseEntity.ok(createSuccessResponse("已上架", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createErrorResponse("操作失败: " + e.getMessage()));
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
