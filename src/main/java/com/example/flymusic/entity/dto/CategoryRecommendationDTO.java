package com.example.flymusic.entity.dto;

import lombok.Data;

@Data
public class CategoryRecommendationDTO {
    private Long id;
    private String title;
    private String cover;
    private String artist;
    private Integer duration;
    private String categoryName;
    private Long playCount;
    
    public CategoryRecommendationDTO(Long id, String title, String cover, String artist, 
                                   Integer duration, String categoryName, Long playCount) {
        this.id = id;
        this.title = title;
        this.cover = cover;
        this.artist = artist;
        this.duration = duration;
        this.categoryName = categoryName;
        this.playCount = playCount;
    }
}
