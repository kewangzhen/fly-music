package com.example.flymusic.entity.dto;

import lombok.Data;
import java.util.List;

@Data
public class HotRankingDTO {
    private String type;
    private String title;
    private String updateTime;
    private List<HotSongDTO> songs;
    
    public HotRankingDTO(String type, String title) {
        this.type = type;
        this.title = title;
    }
}
