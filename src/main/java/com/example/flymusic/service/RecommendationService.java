package com.example.flymusic.service;

import com.example.flymusic.entity.dto.RadarPlaylistDTO;
import com.example.flymusic.entity.dto.UserInterestDTO;

public interface RecommendationService {
    
    void recordBehavior(Long userId, Long songId, Integer behaviorType);
    
    UserInterestDTO getUserInterestProfile(Long userId);
    
    RadarPlaylistDTO getRadarPlaylist(Long userId);
    
    void calculateUserInterestProfile(Long userId);
    
    void generateRadarPlaylist(Long userId);
    
    void dailyRadarGeneration();
}
