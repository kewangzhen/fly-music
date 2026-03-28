package com.example.flymusic.service;

import com.example.flymusic.entity.AiMusicGeneration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

public interface AiMusicService {
    Future<AiMusicGeneration> generateMusicAsync(Long userId, String prompt, String genre, String mood, Integer duration);

    AiMusicGeneration getGenerationStatus(Long id);

    List<AiMusicGeneration> getUserGenerations(Long userId);

    Map<String, Object> recognizeMusicStyle(String audioUrl);

    void deleteGeneration(Long id);
}
