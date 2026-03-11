package com.example.flymusic.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.util.Date;

/**
 * AI音乐生成实体类
 * 用于记录用户AI生成的音乐
 */
@Data
@Entity
@Table(name = "ai_music_generations")
public class AiMusicGeneration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "prompt", columnDefinition = "TEXT", nullable = false)
    private String prompt;
    
    @Column(name = "music_url", length = 255)
    private String musicUrl;
    
    @Column(name = "cover", length = 255)
    private String cover;
    
    @Column(name = "duration")
    private Integer duration;
    
    @Column(name = "genre", length = 50)
    private String genre;
    
    @Column(name = "mood", length = 50)
    private String mood;
    
    @Column(name = "status", columnDefinition = "TINYINT DEFAULT 0")
    private Integer status;
    
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
    
    @Column(name = "created_at", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Date createdAt;
    
    @Column(name = "updated_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private Date updatedAt;
}