package com.example.flymusic.entity.dto;

import lombok.Data;
import java.util.Date;
import java.util.List;

/**
 * 雷达歌单DTO
 * 用于返回雷达歌单数据给前端
 * 
 * @author fly-music
 * @since 2026-03-12
 */
@Data
public class RadarPlaylistDTO {
    
    /**
     * 雷达歌单ID
     */
    private Long id;
    
    /**
     * 歌曲列表
     */
    private List<SongDTO> songs;
    
    /**
     * 兴趣摘要
     * 包含Top标签和各标签分数
     */
    private InterestSummary interestSummary;
    
    /**
     * 生成时间
     */
    private Date generatedAt;
    
    /**
     * 过期时间
     */
    private Date expiresAt;
    
    /**
     * 歌曲DTO内部类
     */
    @Data
    public static class SongDTO {
        
        /**
         * 歌曲ID
         */
        private Long id;
        
        /**
         * 歌曲标题
         */
        private String title;
        
        /**
         * 封面图片URL
         */
        private String cover;
        
        /**
         * 艺术家名称
         */
        private String artist;
        
        /**
         * 时长（秒）
         */
        private Integer duration;
        
        /**
         * 构造函数
         */
        public SongDTO(Long id, String title, String cover, String artist, Integer duration) {
            this.id = id;
            this.title = title;
            this.cover = cover;
            this.artist = artist;
            this.duration = duration;
        }
    }
    
    /**
     * 兴趣摘要内部类
     */
    @Data
    public static class InterestSummary {
        
        /**
         * Top标签（分数最高的标签）
         */
        private String topTag;
        
        /**
         * 标签列表
         */
        private List<InterestTag> tags;
        
        /**
         * 构造函数
         */
        public InterestSummary(String topTag, List<InterestTag> tags) {
            this.topTag = topTag;
            this.tags = tags;
        }
    }
    
    /**
     * 兴趣标签内部类
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
         */
        public InterestTag(String name, Double score) {
            this.name = name;
            this.score = score;
        }
    }
}
