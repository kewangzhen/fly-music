package com.example.flymusic.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.util.Date;

/**
 * 用户收藏实体类
 * 支持收藏歌曲、歌单、歌手、专辑等多种类型
 */
@Data
@Entity
@Table(name = "user_favorites")
public class UserFavorite {
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
    
    @Column(name = "created_at", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Date createdAt;
}