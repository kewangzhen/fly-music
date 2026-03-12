package com.example.flymusic.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.util.Date;

/**
 * 用户行为实体类
 * 用于记录用户对歌曲的各种行为操作，如播放、收藏、分享等
 * 
 * @author fly-music
 * @since 2026-03-12
 */
@Data
@Entity
@Table(name = "user_behavior")
public class UserBehavior {
    
    /**
     * 主键ID
     * 自增生成
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 用户ID
     * 关联用户表，标识行为所属用户
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    /**
     * 歌曲ID
     * 关联歌曲表，标识被操作的歌曲
     */
    @Column(name = "song_id", nullable = false)
    private Long songId;
    
    /**
     * 行为类型
     * 1-播放 2-完整收听 3-收藏 4-分享 5-切歌 6-不喜欢
     * @see com.example.flymusic.entity.enums.BehaviorType
     */
    @Column(name = "behavior_type", nullable = false)
    private Integer behaviorType;
    
    /**
     * 权重分数
     * 根据行为类型计算的分值，用于推荐算法
     * 播放+1, 完整收听+2, 收藏+5, 分享+3, 切歌-3, 不喜欢-5
     */
    @Column(name = "weight")
    private Integer weight;
    
    /**
     * 创建时间
     * 记录行为发生的时间
     */
    @Column(name = "created_at", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Date createdAt;
}
