package com.example.flymusic.controller;

import com.example.flymusic.entity.*;
import com.example.flymusic.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/social")
public class SocialController {

    @Autowired
    private UserFollowRepository userFollowRepository;
    
    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private PostLikeRepository postLikeRepository;
    
    @Autowired
    private PostCommentRepository postCommentRepository;
    
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/follow")
    public ResponseEntity<Map<String, Object>> followUser(@RequestBody Map<String, Long> data) {
        Long followerId = data.get("followerId");
        Long followedId = data.get("followedId");
        
        if (followerId.equals(followedId)) {
            return ResponseEntity.badRequest().body(createErrorResponse("不能关注自己"));
        }
        
        if (userFollowRepository.existsByFollowerIdAndFollowedId(followerId, followedId)) {
            return ResponseEntity.badRequest().body(createErrorResponse("已关注"));
        }
        
        User follower = userRepository.findById(followerId).orElse(null);
        User followed = userRepository.findById(followedId).orElse(null);
        
        if (follower == null || followed == null) {
            return ResponseEntity.badRequest().body(createErrorResponse("用户不存在"));
        }
        
        UserFollow userFollow = new UserFollow();
        userFollow.setFollower(follower);
        userFollow.setFollowed(followed);
        userFollowRepository.save(userFollow);
        
        Map<String, Object> result = new HashMap<>();
        result.put("message", "关注成功");
        return ResponseEntity.ok(createSuccessResponse("关注成功", result));
    }

    @DeleteMapping("/follow")
    public ResponseEntity<Map<String, Object>> unfollowUser(@RequestBody Map<String, Long> data) {
        Long followerId = data.get("followerId");
        Long followedId = data.get("followedId");
        
        Optional<UserFollow> followOpt = userFollowRepository.findByFollowerIdAndFollowedId(followerId, followedId);
        if (followOpt.isPresent()) {
            userFollowRepository.delete(followOpt.get());
        }
        
        return ResponseEntity.ok(createSuccessResponse("取消关注成功", null));
    }

    @GetMapping("/followers/{userId}")
    public ResponseEntity<Map<String, Object>> getFollowers(@PathVariable Long userId) {
        List<UserFollow> followers = userFollowRepository.findFollowers(userId);
        List<Map<String, Object>> followerList = new ArrayList<>();
        
        for (UserFollow uf : followers) {
            followerList.add(convertToUserInfo(uf.getFollower()));
        }
        
        return ResponseEntity.ok(createSuccessResponse("获取成功", followerList));
    }

    @GetMapping("/following/{userId}")
    public ResponseEntity<Map<String, Object>> getFollowing(@PathVariable Long userId) {
        List<UserFollow> following = userFollowRepository.findFollowing(userId);
        List<Map<String, Object>> followingList = new ArrayList<>();
        
        for (UserFollow uf : following) {
            followingList.add(convertToUserInfo(uf.getFollowed()));
        }
        
        return ResponseEntity.ok(createSuccessResponse("获取成功", followingList));
    }

    @GetMapping("/followers/{userId}/count")
    public ResponseEntity<Map<String, Object>> countFollowers(@PathVariable Long userId) {
        long count = userFollowRepository.countByFollowedId(userId);
        Map<String, Object> result = new HashMap<>();
        result.put("count", count);
        return ResponseEntity.ok(createSuccessResponse("获取成功", result));
    }

    @GetMapping("/following/{userId}/count")
    public ResponseEntity<Map<String, Object>> countFollowing(@PathVariable Long userId) {
        long count = userFollowRepository.countByFollowerId(userId);
        Map<String, Object> result = new HashMap<>();
        result.put("count", count);
        return ResponseEntity.ok(createSuccessResponse("获取成功", result));
    }

    @GetMapping("/check-follow")
    public ResponseEntity<Map<String, Object>> checkFollow(
            @RequestParam Long followerId,
            @RequestParam Long followedId) {
        boolean exists = userFollowRepository.existsByFollowerIdAndFollowedId(followerId, followedId);
        Map<String, Object> result = new HashMap<>();
        result.put("following", exists);
        return ResponseEntity.ok(createSuccessResponse("检查成功", result));
    }

    @GetMapping("/followers/{userId}/list")
    public ResponseEntity<Map<String, Object>> getFollowersList(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<UserFollow> followers = userFollowRepository.findFollowers(userId);
        List<Map<String, Object>> followerList = new ArrayList<>();
        
        for (UserFollow uf : followers) {
            followerList.add(convertToUserInfo(uf.getFollower()));
        }
        
        return ResponseEntity.ok(createSuccessResponse("获取成功", followerList));
    }

    @GetMapping("/following/{userId}/list")
    public ResponseEntity<Map<String, Object>> getFollowingList(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<UserFollow> following = userFollowRepository.findFollowing(userId);
        List<Map<String, Object>> followingList = new ArrayList<>();
        
        for (UserFollow uf : following) {
            followingList.add(convertToUserInfo(uf.getFollowed()));
        }
        
        return ResponseEntity.ok(createSuccessResponse("获取成功", followingList));
    }

    @PostMapping("/post")
    public ResponseEntity<Map<String, Object>> createPost(@RequestBody Map<String, Object> data) {
        Long userId = Long.valueOf(data.get("userId").toString());
        String content = data.get("content").toString();
        
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body(createErrorResponse("用户不存在"));
        }
        
        Post post = new Post();
        post.setUser(user);
        post.setContent(content);
        
        if (data.containsKey("songId") && data.get("songId") != null) {
            post.setSongId(Long.valueOf(data.get("songId").toString()));
        }
        
        if (data.containsKey("images") && data.get("images") != null) {
            post.setImages(data.get("images").toString());
        }
        
        post.setLikes(0);
        post.setCommentsCount(0);
        post.setSharesCount(0);
        post.setStatus(1);
        
        postRepository.save(post);
        
        Map<String, Object> result = convertToPostInfo(post);
        return ResponseEntity.status(HttpStatus.CREATED).body(createSuccessResponse("发布成功", result));
    }

    @DeleteMapping("/post/{postId}")
    public ResponseEntity<Map<String, Object>> deletePost(
            @PathVariable Long postId,
            @RequestParam Long userId) {
        Optional<Post> postOpt = postRepository.findById(postId);
        if (!postOpt.isPresent()) {
            return ResponseEntity.badRequest().body(createErrorResponse("动态不存在"));
        }
        
        Post post = postOpt.get();
        if (!post.getUser().getId().equals(userId)) {
            return ResponseEntity.badRequest().body(createErrorResponse("无权删除"));
        }
        
        postRepository.delete(post);
        return ResponseEntity.ok(createSuccessResponse("删除成功", null));
    }

    @GetMapping("/post/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserPosts(@PathVariable Long userId) {
        List<Post> posts = postRepository.findByUserIdOrderByCreatedAtDesc(userId);
        List<Map<String, Object>> postList = posts.stream()
                .map(this::convertToPostInfo)
                .collect(Collectors.toList());
        return ResponseEntity.ok(createSuccessResponse("获取成功", postList));
    }

    @GetMapping("/post/following/{userId}")
    public ResponseEntity<Map<String, Object>> getFollowingPosts(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "20") int limit) {
        List<Post> posts = postRepository.findFollowingPosts(userId, PageRequest.of(0, limit));
        List<Map<String, Object>> postList = posts.stream()
                .map(this::convertToPostInfo)
                .collect(Collectors.toList());
        return ResponseEntity.ok(createSuccessResponse("获取成功", postList));
    }

    @GetMapping("/post/popular")
    public ResponseEntity<Map<String, Object>> getPopularPosts(
            @RequestParam(defaultValue = "20") int limit) {
        List<Post> posts = postRepository.findPopularPosts(PageRequest.of(0, limit));
        List<Map<String, Object>> postList = posts.stream()
                .map(this::convertToPostInfo)
                .collect(Collectors.toList());
        return ResponseEntity.ok(createSuccessResponse("获取成功", postList));
    }

    @GetMapping("/post/all")
    public ResponseEntity<Map<String, Object>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<Post> posts = postRepository.findByStatusOrderByCreatedAtDesc(1);
        int start = page * size;
        int end = Math.min(start + size, posts.size());
        
        List<Map<String, Object>> postList = new ArrayList<>();
        if (start < posts.size()) {
            for (int i = start; i < end; i++) {
                postList.add(convertToPostInfo(posts.get(i)));
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("posts", postList);
        result.put("total", posts.size());
        result.put("page", page);
        result.put("size", size);
        
        return ResponseEntity.ok(createSuccessResponse("获取成功", result));
    }

    @PostMapping("/post/{postId}/like")
    public ResponseEntity<Map<String, Object>> likePost(
            @PathVariable Long postId,
            @RequestBody Map<String, Long> data) {
        Long userId = data.get("userId");
        
        Optional<Post> postOpt = postRepository.findById(postId);
        if (!postOpt.isPresent()) {
            return ResponseEntity.badRequest().body(createErrorResponse("动态不存在"));
        }
        
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body(createErrorResponse("用户不存在"));
        }
        
        if (postLikeRepository.existsByUserIdAndPostId(userId, postId)) {
            return ResponseEntity.badRequest().body(createErrorResponse("已点赞"));
        }
        
        PostLike like = new PostLike();
        like.setUser(user);
        like.setPost(postOpt.get());
        postLikeRepository.save(like);
        
        Post post = postOpt.get();
        post.setLikes(post.getLikes() + 1);
        postRepository.save(post);
        
        Map<String, Object> result = new HashMap<>();
        result.put("liked", true);
        result.put("likes", post.getLikes());
        
        return ResponseEntity.ok(createSuccessResponse("点赞成功", result));
    }

    @DeleteMapping("/post/{postId}/like")
    public ResponseEntity<Map<String, Object>> unlikePost(
            @PathVariable Long postId,
            @RequestParam Long userId) {
        Optional<PostLike> likeOpt = postLikeRepository.findByUserIdAndPostId(userId, postId);
        if (!likeOpt.isPresent()) {
            return ResponseEntity.badRequest().body(createErrorResponse("未点赞"));
        }
        
        postLikeRepository.delete(likeOpt.get());
        
        Optional<Post> postOpt = postRepository.findById(postId);
        if (postOpt.isPresent()) {
            Post post = postOpt.get();
            post.setLikes(Math.max(0, post.getLikes() - 1));
            postRepository.save(post);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("liked", false);
        
        return ResponseEntity.ok(createSuccessResponse("取消点赞成功", result));
    }

    @GetMapping("/post/{postId}/like/status")
    public ResponseEntity<Map<String, Object>> checkLikeStatus(
            @PathVariable Long postId,
            @RequestParam Long userId) {
        boolean liked = postLikeRepository.existsByUserIdAndPostId(userId, postId);
        long likeCount = postLikeRepository.countByPostId(postId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("liked", liked);
        result.put("likes", likeCount);
        
        return ResponseEntity.ok(createSuccessResponse("获取成功", result));
    }

    @PostMapping("/post/{postId}/comment")
    public ResponseEntity<Map<String, Object>> addComment(
            @PathVariable Long postId,
            @RequestBody Map<String, Object> data) {
        Long userId = Long.valueOf(data.get("userId").toString());
        String content = data.get("content").toString();
        
        Optional<Post> postOpt = postRepository.findById(postId);
        if (!postOpt.isPresent()) {
            return ResponseEntity.badRequest().body(createErrorResponse("动态不存在"));
        }
        
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body(createErrorResponse("用户不存在"));
        }
        
        PostComment comment = new PostComment();
        comment.setUser(user);
        comment.setPost(postOpt.get());
        comment.setContent(content);
        
        if (data.containsKey("parentId") && data.get("parentId") != null) {
            comment.setParentId(Long.valueOf(data.get("parentId").toString()));
        }
        
        if (data.containsKey("replyToUserId") && data.get("replyToUserId") != null) {
            comment.setReplyToUserId(Long.valueOf(data.get("replyToUserId").toString()));
        }
        
        comment.setLikes(0);
        postCommentRepository.save(comment);
        
        Post post = postOpt.get();
        post.setCommentsCount(post.getCommentsCount() + 1);
        postRepository.save(post);
        
        Map<String, Object> result = convertToCommentInfo(comment);
        return ResponseEntity.status(HttpStatus.CREATED).body(createSuccessResponse("评论成功", result));
    }

    @GetMapping("/post/{postId}/comments")
    public ResponseEntity<Map<String, Object>> getComments(@PathVariable Long postId) {
        List<PostComment> comments = postCommentRepository.findTopLevelComments(postId);
        List<Map<String, Object>> commentList = new ArrayList<>();
        
        for (PostComment comment : comments) {
            Map<String, Object> commentInfo = convertToCommentInfo(comment);
            
            List<PostComment> replies = postCommentRepository.findByParentIdOrderByCreatedAtAsc(comment.getId());
            List<Map<String, Object>> replyList = replies.stream()
                    .map(this::convertToCommentInfo)
                    .collect(Collectors.toList());
            commentInfo.put("replies", replyList);
            
            commentList.add(commentInfo);
        }
        
        return ResponseEntity.ok(createSuccessResponse("获取成功", commentList));
    }

    @DeleteMapping("/post/comment/{commentId}")
    public ResponseEntity<Map<String, Object>> deleteComment(
            @PathVariable Long commentId,
            @RequestParam Long userId) {
        Optional<PostComment> commentOpt = postCommentRepository.findById(commentId);
        if (!commentOpt.isPresent()) {
            return ResponseEntity.badRequest().body(createErrorResponse("评论不存在"));
        }
        
        PostComment comment = commentOpt.get();
        if (!comment.getUser().getId().equals(userId)) {
            return ResponseEntity.badRequest().body(createErrorResponse("无权删除"));
        }
        
        Optional<Post> postOpt = postRepository.findById(comment.getPost().getId());
        if (postOpt.isPresent()) {
            Post post = postOpt.get();
            post.setCommentsCount(Math.max(0, post.getCommentsCount() - 1));
            postRepository.save(post);
        }
        
        postCommentRepository.delete(comment);
        return ResponseEntity.ok(createSuccessResponse("删除成功", null));
    }

    private Map<String, Object> convertToUserInfo(User user) {
        Map<String, Object> info = new HashMap<>();
        info.put("id", user.getId());
        info.put("username", user.getUsername());
        info.put("nickname", user.getNickname());
        info.put("avatar", user.getAvatar());
        info.put("description", user.getDescription());
        return info;
    }

    private Map<String, Object> convertToPostInfo(Post post) {
        Map<String, Object> info = new HashMap<>();
        info.put("id", post.getId());
        info.put("content", post.getContent());
        info.put("songId", post.getSongId());
        info.put("images", post.getImages());
        info.put("likes", post.getLikes());
        info.put("commentsCount", post.getCommentsCount());
        info.put("sharesCount", post.getSharesCount());
        info.put("createdAt", post.getCreatedAt());
        
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", post.getUser().getId());
        userInfo.put("username", post.getUser().getUsername());
        userInfo.put("nickname", post.getUser().getNickname());
        userInfo.put("avatar", post.getUser().getAvatar());
        info.put("user", userInfo);
        
        return info;
    }

    private Map<String, Object> convertToCommentInfo(PostComment comment) {
        Map<String, Object> info = new HashMap<>();
        info.put("id", comment.getId());
        info.put("content", comment.getContent());
        info.put("parentId", comment.getParentId());
        info.put("replyToUserId", comment.getReplyToUserId());
        info.put("likes", comment.getLikes());
        info.put("createdAt", comment.getCreatedAt());
        
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", comment.getUser().getId());
        userInfo.put("username", comment.getUser().getUsername());
        userInfo.put("nickname", comment.getUser().getNickname());
        userInfo.put("avatar", comment.getUser().getAvatar());
        info.put("user", userInfo);
        
        return info;
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
