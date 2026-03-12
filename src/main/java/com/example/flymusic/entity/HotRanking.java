package com.example.flymusic.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.util.Date;

/**
 * 热门榜单实体类
 * 存储各类热门榜单的歌曲排名信息
 * 
 * 榜单类型：
 * - global: 全局热门榜
 * - category: 分类热门榜
 * - new: 新歌榜
 * - soaring: 飙升榜
 * 
 * @author fly-music
 * @since 2026-03-12
 */
@Data
@Entity
@Table(name = "hot_ranking")
public class HotRanking {
    
    /**
     * 主键ID
     * 自增生成
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 榜单类型
     * 取值: global-全局热门榜, category-分类热门榜, new-新歌榜, soaring-飙升榜
     * @see com.example.flymusic.entity.enums.RankingType
     */
    @Column(name = "ranking_type", nullable = false, length = 50)
    private String rankingType;
    
    /**
     * 分类ID
     * 仅分类热门榜使用，标识所属分类
     */
    @Column(name = "category_id")
    private Long categoryId;
    
    /**
     * 歌曲ID
     * 关联歌曲表，标识榜单中的歌曲
     */
    @Column(name = "song_id", nullable = false)
    private Long songId;
    
    /**
     * 排名
     * 歌曲在榜单中的排名，从1开始
     */
    @Column(name = "rank", nullable = false)
    private Integer rank;
    
    /**
     * 热度得分
     * 歌曲的热度分数
     */
    @Column(name = "hot_score", columnDefinition = "DECIMAL(10,2)")
    private Double hotScore;
    
    /**
     * 创建时间
     * 记录榜单生成时间
     */
    @Column(name = "created_at", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Date createdAt;
}
