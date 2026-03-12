package com.example.flymusic.service;

import com.example.flymusic.entity.dto.AdminRecommendationDTO;

public interface AdminRecommendationService {

    AdminRecommendationDTO getRecommendationData();

    AdminRecommendationDTO.RecommendationConfig getConfig();

    void updateConfig(AdminRecommendationDTO.RecommendationConfig config);

    void recalculateWithNewWeights();
}
