package com.example.flymusic.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.util.Date;

/**
 * 动态帖子实体类
 * 用于用户发布分享音乐的心情、状态等
 */
@Data
@Entity
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;
    
    @Column(name = "song_id")
    private Long songId;
    
    @Column(name = "images", length = 1000)
    private String images;
    
    @Column(name = "likes", columnDefinition = "INT DEFAULT 0")
    private Integer likes;
    
    @Column(name = "comments_count", columnDefinition = "INT DEFAULT 0")
    private Integer commentsCount;
    
    @Column(name = "shares_count", columnDefinition = "INT DEFAULT 0")
    private Integer sharesCount;
    
    @Column(name = "status", columnDefinition = "TINYINT DEFAULT 1")
    private Integer status;
    
    @Column(name = "created_at", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Date createdAt;
    
    @Column(name = "updated_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private Date updatedAt;
}