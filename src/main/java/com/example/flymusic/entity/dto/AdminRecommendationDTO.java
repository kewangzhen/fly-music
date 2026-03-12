package com.example.flymusic.entity.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class AdminRecommendationDTO {

    private CategoryDistribution categoryDistribution;

    private List<TopArtist> topArtists;

    private ActivityStats activityStats;

    private RecommendationConfig config;

    @Data
    public static class CategoryDistribution {
        private List<CategoryItem> categories;

        @Data
        public static class CategoryItem {
            private String name;
            private Long playCount;
            private Integer percentage;
        }
    }

    @Data
    public static class TopArtist {
        private Long artistId;
        private String name;
        private String avatar;
        private Long playCount;
        private Integer rank;
    }

    @Data
    public static class ActivityStats {
        private Long todayPlays;
        private Double todayHours;
        private Integer activeUsers;
        private Long totalSongs;
        private Integer totalUsers;
    }

    @Data
    public static class RecommendationConfig {
        private Integer radarWeight;
        private Integer hotWeight;
        private Integer categoryWeight;
        private Integer similarWeight;
    }
}
