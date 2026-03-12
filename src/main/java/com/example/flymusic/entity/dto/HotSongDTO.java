package com.example.flymusic.entity.dto;

import lombok.Data;

@Data
public class HotSongDTO {
    private Integer rank;
    private Long songId;
    private String title;
    private String cover;
    private String artist;
    private Long playCount;
    private Double hotScore;
    private String trend;
    private Double riseRate;
    
    public HotSongDTO(Integer rank, Long songId, String title, String cover, 
                      String artist, Long playCount, Double hotScore) {
        this.rank = rank;
        this.songId = songId;
        this.title = title;
        this.cover = cover;
        this.artist = artist;
        this.playCount = playCount;
        this.hotScore = hotScore;
        this.trend = "same";
    }
}
