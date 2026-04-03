package com.example.flymusic.controller;

import com.example.flymusic.entity.PlayHistory;
import com.example.flymusic.entity.Song;
import com.example.flymusic.entity.User;
import com.example.flymusic.repository.PlayHistoryRepository;
import com.example.flymusic.repository.SongRepository;
import com.example.flymusic.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
public class ReportController {

    @Autowired
    private PlayHistoryRepository playHistoryRepository;

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/report/listening")
    public ResponseEntity<Map<String, Object>> getListeningReport(@RequestParam Long userId) {
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (!userOpt.isPresent()) {
                return ResponseEntity.badRequest().body(createErrorResponse("用户不存在"));
            }

            List<PlayHistory> histories = playHistoryRepository.findByUserIdOrderByPlayedAtDesc(userId);

            Map<String, Object> report = new HashMap<>();

            report.put("totalPlayCount", histories.size());

            long totalDuration = histories.stream()
                    .filter(h -> h.getPlayDuration() != null)
                    .mapToLong(h -> h.getPlayDuration())
                    .sum();
            report.put("totalListenTime", totalDuration);
            report.put("totalListenMinutes", totalDuration / 60);

            Map<Long, Long> songCountMap = histories.stream()
                    .filter(h -> h.getSong() != null)
                    .collect(Collectors.groupingBy(
                            h -> h.getSong().getId(),
                            Collectors.counting()
                    ));
            List<Map.Entry<Long, Long>> topSongs = songCountMap.entrySet().stream()
                    .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                    .limit(10)
                    .collect(Collectors.toList());

            List<Map<String, Object>> topSongList = new ArrayList<>();
            for (Map.Entry<Long, Long> entry : topSongs) {
                Optional<Song> songOpt = songRepository.findById(entry.getKey());
                if (songOpt.isPresent()) {
                    Song song = songOpt.get();
                    Map<String, Object> songInfo = new HashMap<>();
                    songInfo.put("id", song.getId());
                    songInfo.put("title", song.getTitle());
                    songInfo.put("cover", song.getCover());
                    songInfo.put("playCount", entry.getValue());
                    topSongList.add(songInfo);
                }
            }
            report.put("topSongs", topSongList);

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, -7);
            Date weekAgo = cal.getTime();
            List<PlayHistory> weekHistories = histories.stream()
                    .filter(h -> h.getPlayedAt() != null && h.getPlayedAt().after(weekAgo))
                    .collect(Collectors.toList());
            report.put("weekPlayCount", weekHistories.size());

            cal.add(Calendar.DAY_OF_MONTH, -23);
            Date monthAgo = cal.getTime();
            List<PlayHistory> monthHistories = histories.stream()
                    .filter(h -> h.getPlayedAt() != null && h.getPlayedAt().after(monthAgo))
                    .collect(Collectors.toList());
            report.put("monthPlayCount", monthHistories.size());

            Set<Long> uniqueSongIds = histories.stream()
                    .filter(h -> h.getSong() != null)
                    .map(h -> h.getSong().getId())
                    .collect(Collectors.toSet());
            report.put("uniqueSongCount", uniqueSongIds.size());

            return ResponseEntity.ok(createSuccessResponse("获取成功", report));
        } catch (Exception e) {
            e.printStackTrace();
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
        response.put("code", 400);
        response.put("message", message);
        return response;
    }
}
