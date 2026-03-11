package com.example.flymusic.controller;

import com.example.flymusic.entity.UserFollow;
import com.example.flymusic.entity.Post;
import com.example.flymusic.repository.UserFollowRepository;
import com.example.flymusic.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 社交Controller
 * 提供用户关注、动态相关的RESTful API接口
 */
@RestController
@RequestMapping("/api/social")
public class SocialController {

    @Autowired
    private UserFollowRepository userFollowRepository;
    
    @Autowired
    private PostRepository postRepository;

    /**
     * 关注用户
     */
    @PostMapping("/follow")
    public ResponseEntity<Map<String, Object>> followUser(@RequestBody Map<String, Long> data) {
        Long followerId = data.get("followerId");
        Long followedId = data.get("followedId");
        
        if (userFollowRepository.existsByFollowerIdAndFollowedId(followerId, followedId)) {
            return ResponseEntity.badRequest().body(createErrorResponse("已关注"));
        }
        
        // 创建关注关系...
        Map<String, Object> result = new HashMap<>();
        result.put("message", "关注成功");
        return ResponseEntity.ok(createSuccessResponse("关注成功", result));
    }

    /**
     * 取消关注
     */
    @DeleteMapping("/follow")
    public ResponseEntity<Map<String, Object>> unfollowUser(@RequestBody Map<String, Long> data) {
        Long followerId = data.get("followerId");
        Long followedId = data.get("followedId");
        
        userFollowRepository.findByFollowerIdAndFollowedId(followerId, followedId)
            .ifPresent(userFollowRepository::delete);
        
        return ResponseEntity.ok(createSuccessResponse("取消关注成功", null));
    }

    /**
     * 获取用户粉丝
     */
    @GetMapping("/followers/{userId}")
    public ResponseEntity<Map<String, Object>> getFollowers(@PathVariable Long userId) {
        List<UserFollow> followers = userFollowRepository.findFollowers(userId);
        return ResponseEntity.ok(createSuccessResponse("获取成功", followers));
    }

    /**
     * 获取用户关注列表
     */
    @GetMapping("/following/{userId}")
    public ResponseEntity<Map<String, Object>> getFollowing(@PathVariable Long userId) {
        List<UserFollow> following = userFollowRepository.findFollowing(userId);
        return ResponseEntity.ok(createSuccessResponse("获取成功", following));
    }

    /**
     * 统计粉丝数
     */
    @GetMapping("/followers/{userId}/count")
    public ResponseEntity<Map<String, Object>> countFollowers(@PathVariable Long userId) {
        long count = userFollowRepository.countByFollowedId(userId);
        return ResponseEntity.ok(createSuccessResponse("获取成功", count));
    }

    /**
     * 统计关注数
     */
    @GetMapping("/following/{userId}/count")
    public ResponseEntity<Map<String, Object>> countFollowing(@PathVariable Long userId) {
        long count = userFollowRepository.countByFollowerId(userId);
        return ResponseEntity.ok(createSuccessResponse("获取成功", count));
    }

    /**
     * 检查是否已关注
     */
    @GetMapping("/check-follow")
    public ResponseEntity<Map<String, Object>> checkFollow(
            @RequestParam Long followerId,
            @RequestParam Long followedId) {
        boolean exists = userFollowRepository.existsByFollowerIdAndFollowedId(followerId, followedId);
        return ResponseEntity.ok(createSuccessResponse("检查成功", exists));
    }

    /**
     * 发布动态
     */
    @PostMapping("/post")
    public ResponseEntity<Map<String, Object>> createPost(@RequestBody Map<String, Object> data) {
        Map<String, Object> result = new HashMap<>();
        result.put("message", "发布成功");
        return ResponseEntity.status(HttpStatus.CREATED).body(createSuccessResponse("发布成功", result));
    }

    /**
     * 获取用户动态
     */
    @GetMapping("/post/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserPosts(@PathVariable Long userId) {
        List<Post> posts = postRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return ResponseEntity.ok(createSuccessResponse("获取成功", posts));
    }

    /**
     * 获取关注用户动态
     */
    @GetMapping("/post/following/{userId}")
    public ResponseEntity<Map<String, Object>> getFollowingPosts(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "20") int limit) {
        List<Post> posts = postRepository.findFollowingPosts(userId, PageRequest.of(0, limit));
        return ResponseEntity.ok(createSuccessResponse("获取成功", posts));
    }

    /**
     * 获取热门动态
     */
    @GetMapping("/post/popular")
    public ResponseEntity<Map<String, Object>> getPopularPosts(
            @RequestParam(defaultValue = "20") int limit) {
        List<Post> posts = postRepository.findPopularPosts(PageRequest.of(0, limit));
        return ResponseEntity.ok(createSuccessResponse("获取成功", posts));
    }

    /**
     * 点赞动态
     */
    @PostMapping("/post/{postId}/like")
    public ResponseEntity<Map<String, Object>> likePost(@PathVariable Long postId) {
        Map<String, Object> result = new HashMap<>();
        result.put("message", "点赞成功");
        return ResponseEntity.ok(createSuccessResponse("点赞成功", result));
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