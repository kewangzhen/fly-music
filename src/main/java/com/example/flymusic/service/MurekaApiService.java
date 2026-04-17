package com.example.flymusic.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class MurekaApiService {

    @Value("${ai.mureka.api.key:}")
    private String apiKey;

    @Value("${ai.mureka.api.url:https://api.mureka.cn}")
    private String apiBaseUrl;

    @Value("${song.storage.path:./songs}")
    private String songStoragePath;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public MurekaApiService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public Map<String, Object> generateMusic(String prompt, Integer duration, String model) {
        Map<String, Object> result = new HashMap<>();

        if (apiKey == null || apiKey.isEmpty()) {
            result.put("success", false);
            result.put("error", "Mureka API key 未配置");
            return result;
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("prompt", prompt);
            requestBody.put("model", model != null ? model : "mureka-v6");

            if (duration != null) {
                requestBody.put("duration", duration);
            }

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    apiBaseUrl + "/v1/music/generate",
                    entity,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK || response.getStatusCode() == HttpStatus.ACCEPTED) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                if (jsonNode.has("task_id") || jsonNode.has("id")) {
                    result.put("success", true);
                    result.put("task_id", jsonNode.has("task_id") ? jsonNode.get("task_id").asText() : jsonNode.get("id").asText());
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

    public Map<String, Object> generateWithLyrics(String prompt, String lyrics, Integer duration, String model) {
        Map<String, Object> result = new HashMap<>();

        if (apiKey == null || apiKey.isEmpty()) {
            result.put("success", false);
            result.put("error", "Mureka API key 未配置");
            return result;
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("prompt", prompt);
            requestBody.put("lyrics", lyrics);
            requestBody.put("model", model != null ? model : "mureka-v6");

            if (duration != null) {
                requestBody.put("duration", duration);
            }

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    apiBaseUrl + "/v1/song/generate",
                    entity,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK || response.getStatusCode() == HttpStatus.ACCEPTED) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                if (jsonNode.has("task_id") || jsonNode.has("id")) {
                    result.put("success", true);
                    result.put("task_id", jsonNode.has("task_id") ? jsonNode.get("task_id").asText() : jsonNode.get("id").asText());
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
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + apiKey);

            HttpEntity<?> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    apiBaseUrl + "/v1/music/status/" + taskId,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                String status = jsonNode.has("status") ? jsonNode.get("status").asText() : "";

                result.put("success", true);
                result.put("status", status);

                if ("completed".equals(status) || "success".equals(status)) {
                    if (jsonNode.has("audio_url")) {
                        result.put("audio_url", jsonNode.get("audio_url").asText());
                    }
                    if (jsonNode.has("image_url")) {
                        result.put("image_url", jsonNode.get("image_url").asText());
                    }
                    if (jsonNode.has("lyrics")) {
                        result.put("lyrics", jsonNode.get("lyrics").asText());
                    }
                }

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

    public Map<String, Object> recognizeMusic(MultipartFile audioFile) {
        Map<String, Object> result = new HashMap<>();

        if (apiKey == null || apiKey.isEmpty()) {
            result.put("success", false);
            result.put("error", "Mureka API key 未配置");
            return result;
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.set("Authorization", "Bearer " + apiKey);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", audioFile.getResource());

            HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    apiBaseUrl + "/v1/music/recognize",
                    entity,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());

                result.put("success", true);

                if (jsonNode.has("genre")) {
                    result.put("genre", jsonNode.get("genre").asText());
                }
                if (jsonNode.has("mood")) {
                    result.put("mood", jsonNode.get("mood").asText());
                }
                if (jsonNode.has("tempo")) {
                    result.put("tempo", jsonNode.get("tempo").asInt());
                }
                if (jsonNode.has("instruments")) {
                    result.put("instruments", jsonNode.get("instruments").toString());
                }
                if (jsonNode.has("language")) {
                    result.put("language", jsonNode.get("language").asText());
                }
                if (jsonNode.has("tags")) {
                    result.put("tags", jsonNode.get("tags").toString());
                }

                return result;
            }

            result.put("success", false);
            result.put("error", "识别失败");
            return result;

        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
            return result;
        }
    }

    public Map<String, Object> analyzeMusic(MultipartFile audioFile) {
        Map<String, Object> result = new HashMap<>();

        if (apiKey == null || apiKey.isEmpty()) {
            result.put("success", false);
            result.put("error", "Mureka API key 未配置");
            return result;
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.set("Authorization", "Bearer " + apiKey);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", audioFile.getResource());

            HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    apiBaseUrl + "/v1/music/analyze",
                    entity,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());

                result.put("success", true);
                result.put("data", jsonNode.toString());

                return result;
            }

            result.put("success", false);
            result.put("error", "分析失败");
            return result;

        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
            return result;
        }
    }

    public String downloadAudio(String audioUrl, Long generationId) throws Exception {
        ResponseEntity<byte[]> response = restTemplate.getForEntity(audioUrl, byte[].class);

        if (response.getStatusCode() == HttpStatus.OK) {
            Path songsDir = Paths.get(songStoragePath).toAbsolutePath();
            if (!Files.exists(songsDir)) {
                Files.createDirectories(songsDir);
            }

            String filename = "mureka-" + generationId + ".mp3";
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
