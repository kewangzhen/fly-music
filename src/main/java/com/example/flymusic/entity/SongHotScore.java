package com.example.flymusic.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.util.Date;

/**
 * 歌曲热度统计实体类
 * 存储每首歌曲的热度得分和各维度加权得分
 * 用于热门榜单的计算和展示
 * 
 * 热度算法：
 * 热度得分 = 加权总分 / (发布时间(天) + 2)^1.8
 * 
 * @author fly-music
 * @since 2026-03-12
 */
@Data
@Entity
@Table(name = "song_hot_score")
public class SongHotScore {
    
    /**
     * 主键ID
     * 自增生成
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 歌曲ID
     * 关联歌曲表，每首歌曲有唯一的热度统计记录
     */
    @Column(name = "song_id", nullable = false, unique = true)
    private Long songId;
    
    /**
     * 播放得分
     * 播放次数 × 权重(1)
     */
    @Column(name = "play_score")
    private Integer playScore;
    
    /**
     * 收藏得分
     * 收藏次数 × 权重(5)
     */
    @Column(name = "favorite_score")
    private Integer favoriteScore;
    
    /**
     * 分享得分
     * 分享次数 × 权重(8)
     */
    @Column(name = "share_score")
    private Integer shareScore;
    
    /**
     * 下载得分
     * 下载次数 × 权重(3)
     */
    @Column(name = "download_score")
    private Integer downloadScore;
    
    /**
     * 评论得分
     * 评论次数 × 权重(4)
     */
    @Column(name = "comment_score")
    private Integer commentScore;
    
    /**
     * 加权总分
     * 各维度得分之和 = playScore + favoriteScore + shareScore + downloadScore + commentScore
     */
    @Column(name = "total_weighted_score", columnDefinition = "BIGINT DEFAULT 0")
    private Long totalWeightedScore;
    
    /**
     * 热度得分
     * 经过时间衰减计算后的最终热度得分
     * 算法: 加权总分 / (发布时间(天) + 2)^1.8
     */
    @Column(name = "hot_score", columnDefinition = "DECIMAL(10,2) DEFAULT 0")
    private Double hotScore;
    
    /**
     * 当前排名
     * 基于热度得分的排名
     */
    @Column(name = "rank")
    private Integer rank;
    
    /**
     * 创建时间
     * 记录首次创建时间
     */
    @Column(name = "created_at", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Date createdAt;
    
    /**
     * 更新时间
     * 记录最后更新时间
     */
    @Column(name = "updated_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private Date updatedAt;
}
