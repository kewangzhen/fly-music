package com.example.flymusic.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.util.Date;

/**
 * 雷达歌单实体类
 * 存储每日为用户生成的个性化推荐歌单
 * 根据用户兴趣画像，从Top标签中筛选歌曲组成推荐列表
 * 
 * @author fly-music
 * @since 2026-03-12
 */
@Data
@Entity
@Table(name = "radar_playlist")
public class RadarPlaylist {
    
    /**
     * 主键ID
     * 自增生成
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 用户ID
     * 关联用户表，每个用户有独立的雷达歌单
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    /**
     * 歌曲ID列表
     * JSON格式存储推荐的歌曲ID列表
     * 例如: [1, 2, 3, 4, 5]
     */
    @Column(name = "song_ids", columnDefinition = "JSON")
    private String songIds;
    
    /**
     * 兴趣摘要
     * JSON格式存储用户兴趣标签及分数
     * 例如: {"top_tag": "摇滚", "scores": {"摇滚": 45, "民谣": 22}}
     */
    @Column(name = "interest_summary", columnDefinition = "JSON")
    private String interestSummary;
    
    /**
     * 创建时间
     * 记录雷达歌单生成时间
     */
    @Column(name = "created_at", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Date createdAt;
    
    /**
     * 过期时间
     * 雷达歌单有效期，通常为次日，用于定时刷新
     */
    @Column(name = "expires_at")
    private Date expiresAt;
}
