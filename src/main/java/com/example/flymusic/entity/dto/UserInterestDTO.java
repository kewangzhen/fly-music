package com.example.flymusic.entity.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

/**
 * 用户兴趣画像DTO
 * 用于返回用户兴趣画像数据给前端
 * 
 * @author fly-music
 * @since 2026-03-12
 */
@Data
public class UserInterestDTO {
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 兴趣标签及分数映射
     * 例如: {"摇滚": 45.0, "民谣": 22.0, "流行": 4.0}
     */
    private Map<String, Double> interests;
    
    /**
     * Top标签列表
     * 按分数降序排列的前几个标签
     */
    private List<InterestTag> topTags;
    
    /**
     * 最后更新时间
     */
    private String lastUpdated;
    
    /**
     * 兴趣标签内部类
     * 用于返回标签名称和分数
     */
    @Data
    public static class InterestTag {
        
        /**
         * 标签名称
         */
        private String name;
        
        /**
         * 偏好分数
         */
        private Double score;
        
        /**
         * 构造函数
         * 
         * @param name 标签名称
         * @param score 偏好分数
         */
        public InterestTag(String name, Double score) {
            this.name = name;
            this.score = score;
        }
    }
}
