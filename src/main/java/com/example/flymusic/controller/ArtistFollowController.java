package com.example.flymusic.controller;

import com.example.flymusic.entity.ArtistFollow;
import com.example.flymusic.entity.User;
import com.example.flymusic.repository.ArtistFollowRepository;
import com.example.flymusic.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/social/artist")
public class ArtistFollowController {

    @Autowired
    private ArtistFollowRepository artistFollowRepository;
    
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/follow")
    public ResponseEntity<Map<String, Object>> followArtist(@RequestBody Map<String, Object> data) {
        Long userId = Long.valueOf(data.get("userId").toString());
        Long artistId = Long.valueOf(data.get("artistId").toString());
        String artistName = data.get("artistName") != null ? data.get("artistName").toString() : "";
        String artistAvatar = data.get("artistAvatar") != null ? data.get("artistAvatar").toString() : "";
        
        if (artistFollowRepository.existsByUserIdAndArtistId(userId, artistId)) {
            return ResponseEntity.badRequest().body(createErrorResponse("已关注该歌手"));
        }
        
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body(createErrorResponse("用户不存在"));
        }
        
        ArtistFollow follow = new ArtistFollow();
        follow.setUser(user);
        follow.setArtistId(artistId);
        follow.setArtistName(artistName);
        follow.setArtistAvatar(artistAvatar);
        artistFollowRepository.save(follow);
        
        Map<String, Object> result = new HashMap<>();
        result.put("message", "关注成功");
        return ResponseEntity.ok(createSuccessResponse("关注成功", result));
    }

    @DeleteMapping("/follow")
    public ResponseEntity<Map<String, Object>> unfollowArtist(@RequestBody Map<String, Long> data) {
        Long userId = data.get("userId");
        Long artistId = data.get("artistId");
        
        artistFollowRepository.deleteByUserIdAndArtistId(userId, artistId);
        return ResponseEntity.ok(createSuccessResponse("取消关注成功", null));
    }

    @GetMapping("/following/{userId}")
    public ResponseEntity<Map<String, Object>> getFollowingArtists(@PathVariable Long userId) {
        List<ArtistFollow> follows = artistFollowRepository.findByUserId(userId);
        List<Map<String, Object>> artists = new ArrayList<>();
        
        for (ArtistFollow follow : follows) {
            Map<String, Object> artist = new HashMap<>();
            artist.put("id", follow.getId());
            artist.put("artistId", follow.getArtistId());
            artist.put("artistName", follow.getArtistName());
            artist.put("artistAvatar", follow.getArtistAvatar());
            artist.put("createdAt", follow.getCreatedAt());
            artists.add(artist);
        }
        
        return ResponseEntity.ok(createSuccessResponse("获取成功", artists));
    }

    @GetMapping("/check-follow")
    public ResponseEntity<Map<String, Object>> checkFollow(
            @RequestParam Long userId,
            @RequestParam Long artistId) {
        boolean exists = artistFollowRepository.existsByUserIdAndArtistId(userId, artistId);
        Map<String, Object> result = new HashMap<>();
        result.put("following", exists);
        return ResponseEntity.ok(createSuccessResponse("检查成功", result));
    }

    @GetMapping("/following/{userId}/count")
    public ResponseEntity<Map<String, Object>> countFollowing(@PathVariable Long userId) {
        long count = artistFollowRepository.findByUserId(userId).size();
        Map<String, Object> result = new HashMap<>();
        result.put("count", count);
        return ResponseEntity.ok(createSuccessResponse("获取成功", result));
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
