package com.example.flymusic.entity.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

/**
 * 听歌报告DTO
 * 用于返回用户的听歌报告数据给前端
 * 
 * 包含内容：
 * - 基础统计（听歌曲数、时长、天数、播放次数）
 * - 音乐流派分布（饼图数据）
 * - TOP歌曲列表
 * - TOP歌手列表
 * - 标签分布（词云数据）
 * - 时间分布
 * 
 * @author fly-music
 * @since 2026-03-12
 */
@Data
public class ListeningReportDTO {
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 报告年份
     */
    private Integer year;
    
    /**
     * 基础统计信息
     */
    private Summary summary;
    
    /**
     * 音乐流派分布（饼图数据）
     */
    private List<CategoryDistribution> categoryDistribution;
    
    /**
     * 最喜欢的歌曲TOP10
     */
    private List<TopSong> topSongs;
    
    /**
     * 最喜欢的歌手
     */
    private List<TopArtist> topArtists;
    
    /**
     * 标签分布（词云数据）
     */
    private Map<String, Integer> tagDistribution;
    
    /**
     * 时间分布
     */
    private Map<String, Integer> timeDistribution;
    
    /**
     * 报告生成时间
     */
    private String generatedAt;
    
    /**
     * 基础统计内部类
     */
    @Data
    public static class Summary {
        
        /**
         * 听歌曲数
         * 用户累计播放过的歌曲总数
         */
        private Integer totalSongs;
        
        /**
         * 听歌时长（小时）
         */
        private Double totalHours;
        
        /**
         * 听歌天数
         */
        private Integer totalDays;
        
        /**
         * 播放次数
         */
        private Long totalPlays;
    }
    
    /**
     * 分类分布内部类（饼图）
     */
    @Data
    public static class CategoryDistribution {
        
        /**
         * 分类名称
         */
        private String name;
        
        /**
         * 占比（百分比）
         */
        private Integer value;
        
        public CategoryDistribution(String name, Integer value) {
            this.name = name;
            this.value = value;
        }
    }
    
    /**
     * TOP歌曲内部类
     */
    @Data
    public static class TopSong {
        
        /**
         * 排名
         */
        private Integer rank;
        
        /**
         * 歌曲ID
         */
        private Long songId;
        
        /**
         * 歌曲标题
         */
        private String title;
        
        /**
         * 艺术家
         */
        private String artist;
        
        /**
         * 封面图片
         */
        private String cover;
        
        /**
         * 播放次数
         */
        private Long playCount;
        
        public TopSong(Integer rank, Long songId, String title, String artist, String cover, Long playCount) {
            this.rank = rank;
            this.songId = songId;
            this.title = title;
            this.artist = artist;
            this.cover = cover;
            this.playCount = playCount;
        }
    }
    
    /**
     * TOP歌手内部类
     */
    @Data
    public static class TopArtist {
        
        /**
         * 排名
         */
        private Integer rank;
        
        /**
         * 歌手ID
         */
        private Long artistId;
        
        /**
         * 歌手名称
         */
        private String name;
        
        /**
         * 歌手头像
         */
        private String avatar;
        
        /**
         * 歌曲数量
         */
        private Integer songCount;
        
        /**
         * 播放次数
         */
        private Long playCount;
        
        public TopArtist(Integer rank, Long artistId, String name, String avatar, Integer songCount, Long playCount) {
            this.rank = rank;
            this.artistId = artistId;
            this.name = name;
            this.avatar = avatar;
            this.songCount = songCount;
            this.playCount = playCount;
        }
    }
}
