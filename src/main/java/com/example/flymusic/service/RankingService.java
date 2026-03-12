package com.example.flymusic.service;

import com.example.flymusic.entity.dto.HotRankingDTO;

public interface RankingService {
    
    HotRankingDTO getGlobalRanking();
    
    HotRankingDTO getCategoryRanking(Long categoryId);
    
    HotRankingDTO getNewSongRanking();
    
    HotRankingDTO getSoaringRanking();
    
    void calculateHotScores();
    
    void generateAllRankings();
}
