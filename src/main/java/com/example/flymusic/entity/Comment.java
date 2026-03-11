package com.example.flymusic.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.util.Date;

/**
 * 评论实体类
 * 用于用户对歌曲、歌单、专辑、动态的评论和互动
 */
@Data
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "target_type", nullable = false)
    private Integer targetType;
    
    @Column(name = "target_id", nullable = false)
    private Long targetId;
    
    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;
    
    @Column(name = "parent_id")
    private Long parentId;
    
    @Column(name = "likes", columnDefinition = "INT DEFAULT 0")
    private Integer likes;
    
    @Column(name = "status", columnDefinition = "TINYINT DEFAULT 1")
    private Integer status;
    
    @Column(name = "created_at", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Date createdAt;
    
    @Column(name = "updated_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private Date updatedAt;
}