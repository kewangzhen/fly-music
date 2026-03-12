package com.example.flymusic.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.util.Date;

/**
 * 用户兴趣画像实体类
 * 存储用户对各音乐标签的偏好分数，用于个性化推荐
 * 
 * @author fly-music
 * @since 2026-03-12
 */
@Data
@Entity
@Table(name = "user_interest_profile")
public class UserInterestProfile {
    
    /**
     * 主键ID
     * 自增生成
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 用户ID
     * 关联用户表，每个用户对应唯一的兴趣画像
     */
    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;
    
    /**
     * 兴趣数据
     * JSON格式存储用户对各标签的偏好分数
     * 例如: {"摇滚": 45, "民谣": 22, "流行": 4}
     */
    @Column(name = "interest_data", columnDefinition = "JSON")
    private String interestData;
    
    /**
     * 最后更新时间
     * 记录兴趣画像的最新计算时间
     */
    @Column(name = "last_updated", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private Date lastUpdated;
}
