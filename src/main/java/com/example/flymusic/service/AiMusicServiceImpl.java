package com.example.flymusic.service;

import com.example.flymusic.entity.AiMusicGeneration;
import com.example.flymusic.entity.User;
import com.example.flymusic.repository.AiMusicGenerationRepository;
import com.example.flymusic.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Service
public class AiMusicServiceImpl implements AiMusicService {

    @Autowired
    private AiMusicGenerationRepository aiMusicRepository;

    @Autowired
    private UserRepository userRepository;

    @Value("${ai.api.key:}")
    private String apiKey;

    @Value("${ai.api.provider:musicgen}")
    private String apiProvider;

    @Value("${song.storage.path:./songs}")
    private String songStoragePath;

    @Value("${ai.musicgen.enabled:false}")
    private boolean musicgenEnabled;

    @Value("${ai.musicgen.service.url:http://localhost:5000}")
    private String musicgenServiceUrl;

    @Override
    @Async
    public Future<AiMusicGeneration> generateMusicAsync(Long userId, String prompt, String genre, String mood, Integer duration) {
        AiMusicGeneration generation = new AiMusicGeneration();

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            generation.setErrorMessage("用户不存在");
            generation.setStatus(2);
            return CompletableFuture.completedFuture(generation);
        }

        generation.setUser(userOpt.get());
        generation.setPrompt(prompt);
        generation.setGenre(genre);
        generation.setMood(mood);
        generation.setDuration(duration);
        generation.setStatus(0);

        generation = aiMusicRepository.save(generation);

        try {
            if (musicgenEnabled && "musicgen".equalsIgnoreCase(apiProvider)) {
                generation = callMusicGenService(generation);
            } else {
                Thread.sleep(5000);
                String musicUrl = generateMockMusicUrl(generation.getId());
                String cover = generateMockCover(genre, mood);
                generation.setMusicUrl(musicUrl);
                generation.setCover(cover);
                generation.setStatus(1);
            }
            generation.setErrorMessage(null);
        } catch (InterruptedException e) {
            generation.setStatus(2);
            generation.setErrorMessage("生成过程中断");
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            generation.setStatus(2);
            generation.setErrorMessage("生成失败: " + e.getMessage());
        }

        generation = aiMusicRepository.save(generation);
        return CompletableFuture.completedFuture(generation);
    }

    private AiMusicGeneration callMusicGenService(AiMusicGeneration generation) throws Exception {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("prompt", generation.getPrompt());
        requestBody.put("duration", generation.getDuration() != null ? generation.getDuration() : 30);
        if (generation.getGenre() != null) {
            requestBody.put("genre", generation.getGenre());
        }
        if (generation.getMood() != null) {
            requestBody.put("mood", generation.getMood());
        }

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        String taskId = null;
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    musicgenServiceUrl + "/api/generate",
                    entity,
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.ACCEPTED || response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> responseData = response.getBody();
                if (responseData != null && responseData.get("data") != null) {
                    Map<String, Object> data = (Map<String, Object>) responseData.get("data");
                    taskId = (String) data.get("id");

                    generation.setStatus(0);
                    aiMusicRepository.save(generation);

                    boolean completed = waitForGenerationComplete(taskId, generation);

                    if (completed && generation.getStatus() == 1) {
                        return generation;
                    }
                }
            }
        } catch (Exception e) {
            throw new Exception("调用MusicGen服务失败: " + e.getMessage());
        }

        if (generation.getStatus() != 1) {
            generation.setStatus(2);
            generation.setErrorMessage("MusicGen生成超时或失败");
        }

        return generation;
    }

    private boolean waitForGenerationComplete(String taskId, AiMusicGeneration generation) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        int maxAttempts = 120;
        int attempt = 0;

        while (attempt < maxAttempts) {
            try {
                Thread.sleep(5000);
                attempt++;

                ResponseEntity<Map> statusResponse = restTemplate.getForEntity(
                        musicgenServiceUrl + "/api/status/" + taskId,
                        Map.class
                );

                if (statusResponse.getStatusCode() == HttpStatus.OK) {
                    Map<String, Object> responseData = statusResponse.getBody();
                    if (responseData != null && responseData.get("data") != null) {
                        Map<String, Object> data = (Map<String, Object>) responseData.get("data");
                        String status = (String) data.get("status");

                        if ("completed".equals(status)) {
                            String audioUrl = (String) data.get("audio_url");
                            if (audioUrl != null) {
                                String localUrl = downloadAndSaveAudio(audioUrl, generation.getId());
                                generation.setMusicUrl(localUrl);
                            }
                            generation.setStatus(1);
                            return true;
                        } else if ("failed".equals(status)) {
                            String error = (String) data.get("error");
                            generation.setStatus(2);
                            generation.setErrorMessage(error != null ? error : "生成失败");
                            return false;
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("查询状态失败: " + e.getMessage());
            }
        }

        generation.setErrorMessage("等待生成结果超时");
        generation.setStatus(2);
        return false;
    }

    private String downloadAndSaveAudio(String audioUrl, Long generationId) throws Exception {
        RestTemplate restTemplate = new RestTemplate();

        String downloadUrl = audioUrl;
        if (!audioUrl.startsWith("http")) {
            downloadUrl = musicgenServiceUrl + audioUrl;
        }

        ResponseEntity<byte[]> response = restTemplate.getForEntity(downloadUrl, byte[].class);

        if (response.getStatusCode() == HttpStatus.OK) {
            Path songsDir = Paths.get(songStoragePath).toAbsolutePath();
            if (!Files.exists(songsDir)) {
                Files.createDirectories(songsDir);
            }

            String filename = "aimusic-" + generationId + ".wav";
            Path filePath = songsDir.resolve(filename);
            Files.write(filePath, response.getBody());

            return "/api/songs/file/" + filename;
        }

        return null;
    }

    @Override
    public AiMusicGeneration getGenerationStatus(Long id) {
        return aiMusicRepository.findById(id).orElse(null);
    }

    @Override
    public List<AiMusicGeneration> getUserGenerations(Long userId) {
        return aiMusicRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    public Map<String, Object> recognizeMusicStyle(String audioUrl) {
        Map<String, Object> result = new HashMap<>();

        String[] genres = {"流行", "摇滚", "电子", "古典", "爵士", "民谣", "说唱", "R&B"};
        String[] moods = {"欢快", "悲伤", "平静", "激昂", "浪漫", "神秘"};
        String[] instruments = {"吉他", "钢琴", "鼓", "贝斯", "小提琴", "萨克斯", "合成器"};

        Random random = new Random();
        result.put("genre", genres[random.nextInt(genres.length)]);
        result.put("mood", moods[random.nextInt(moods.length)]);
        result.put("bpm", 60 + random.nextInt(120));
        result.put("instruments", Arrays.asList(
            instruments[random.nextInt(instruments.length)],
            instruments[random.nextInt(instruments.length)]
        ));
        result.put("key", getRandomKey(random));
        result.put("timeSignature", "4/4");
        result.put("energy", random.nextInt(100));

        return result;
    }

    @Override
    public void deleteGeneration(Long id) {
        aiMusicRepository.deleteById(id);
    }

    private String generateMockMusicUrl(Long id) {
        return "/api/songs/file/demo-" + id + ".mp3";
    }

    private String generateMockCover(String genre, String mood) {
        String[] covers = {
            "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=400",
            "https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=400",
            "https://images.unsplash.com/photo-1511379938547-c1f69419868d?w=400",
            "https://images.unsplash.com/photo-1514320291840-2e0a9bf2a9ae?w=400",
            "https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?w=400"
        };
        return covers[new Random().nextInt(covers.length)];
    }

    private String getRandomKey(Random random) {
        String[] keys = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
        String[] modes = {"大调", "小调"};
        return keys[random.nextInt(keys.length)] + " " + modes[random.nextInt(modes.length)];
    }
}
