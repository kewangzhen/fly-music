package com.example.flymusic.service;

import com.example.flymusic.entity.AiMusicGeneration;
import com.example.flymusic.entity.User;
import com.example.flymusic.repository.AiMusicGenerationRepository;
import com.example.flymusic.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class UdioApiService {

    @Value("${ai.udio.api.key:}")
    private String apiKey;

    @Value("${ai.udio.api.url:https://api.udioapi.pro}")
    private String apiBaseUrl;

    @Value("${song.storage.path:./songs}")
    private String songStoragePath;

    public Map<String, Object> generateMusic(String prompt, Integer duration) {
        Map<String, Object> result = new HashMap<>();
        
        if (apiKey == null || apiKey.isEmpty()) {
            result.put("success", false);
            result.put("error", "UDIO API key 未配置");
            return result;
        }

        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("prompt", prompt);
            requestBody.put("make_instrumental", false);
            requestBody.put("wait_for_model", true);

            if (duration != null) {
                requestBody.put("duration", duration);
            }

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    apiBaseUrl + "/udio/v1/generate",
                    entity,
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK || response.getStatusCode() == HttpStatus.ACCEPTED) {
                Map<String, Object> responseData = response.getBody();
                if (responseData != null) {
                    result.put("success", true);
                    result.put("data", responseData);
                    return result;
                }
            }

            result.put("success", false);
            result.put("error", "API 调用失败");
            return result;

        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
            return result;
        }
    }

    public Map<String, Object> getGenerationStatus(String taskId) {
        Map<String, Object> result = new HashMap<>();

        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + apiKey);

            HttpEntity<?> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    apiBaseUrl + "/udio/v1/generate/" + taskId,
                    HttpMethod.GET,
                    entity,
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                result.put("success", true);
                result.put("data", response.getBody());
                return result;
            }

            result.put("success", false);
            result.put("error", "查询失败");
            return result;

        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
            return result;
        }
    }

    public String downloadAudio(String audioUrl, Long generationId) throws Exception {
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<byte[]> response = restTemplate.getForEntity(audioUrl, byte[].class);

        if (response.getStatusCode() == HttpStatus.OK) {
            Path songsDir = Paths.get(songStoragePath).toAbsolutePath();
            if (!Files.exists(songsDir)) {
                Files.createDirectories(songsDir);
            }

            String filename = "aimusic-" + generationId + ".mp3";
            Path filePath = songsDir.resolve(filename);
            Files.write(filePath, response.getBody());

            return "/api/songs/file/" + filename;
        }

        return null;
    }

    public boolean isConfigured() {
        return apiKey != null && !apiKey.isEmpty();
    }
}
