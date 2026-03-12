package com.example.flymusic.service.impl;

import com.example.flymusic.entity.RadarPlaylist;
import com.example.flymusic.entity.Song;
import com.example.flymusic.entity.User;
import com.example.flymusic.entity.UserBehavior;
import com.example.flymusic.entity.UserInterestProfile;
import com.example.flymusic.entity.dto.RadarPlaylistDTO;
import com.example.flymusic.entity.dto.UserInterestDTO;
import com.example.flymusic.entity.enums.BehaviorType;
import com.example.flymusic.repository.RadarPlaylistRepository;
import com.example.flymusic.repository.SongRepository;
import com.example.flymusic.repository.UserBehaviorRepository;
import com.example.flymusic.repository.UserInterestProfileRepository;
import com.example.flymusic.repository.UserRepository;
import com.example.flymusic.service.RecommendationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RecommendationServiceImpl implements RecommendationService {
    
    private static final Logger log = LoggerFactory.getLogger(RecommendationServiceImpl.class);
    private static final int RECENT_DAYS = 30;
    private static final int RADAR_PLAYLIST_SIZE = 20;
    private static final int CATEGORY_SONG_COUNT = 20;
    
    @Autowired
    private UserBehaviorRepository userBehaviorRepository;
    
    @Autowired
    private UserInterestProfileRepository userInterestProfileRepository;
    
    @Autowired
    private RadarPlaylistRepository radarPlaylistRepository;
    
    @Autowired
    private SongRepository songRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Override
    @Transactional
    public void recordBehavior(Long userId, Long songId, Integer behaviorType) {
        BehaviorType type = BehaviorType.fromCode(behaviorType);
        int weight = type.getWeight();
        
        UserBehavior behavior = new UserBehavior();
        behavior.setUserId(userId);
        behavior.setSongId(songId);
        behavior.setBehaviorType(behaviorType);
        behavior.setWeight(weight);
        behavior.setCreatedAt(new Date());
        
        userBehaviorRepository.save(behavior);
        log.info("Recorded behavior: userId={}, songId={}, type={}, weight={}", 
                userId, songId, type, weight);
    }
    
    @Override
    public UserInterestDTO getUserInterestProfile(Long userId) {
        Optional<UserInterestProfile> profileOpt = userInterestProfileRepository.findByUserId(userId);
        
        UserInterestDTO dto = new UserInterestDTO();
        dto.setUserId(userId);
        
        if (profileOpt.isPresent()) {
            UserInterestProfile profile = profileOpt.get();
            try {
                Map<String, Double> interests = objectMapper.readValue(
                        profile.getInterestData(), 
                        new TypeReference<Map<String, Double>>() {});
                dto.setInterests(interests);
                dto.setLastUpdated(profile.getLastUpdated().toString());
                
                List<UserInterestDTO.InterestTag> topTags = interests.entrySet().stream()
                        .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                        .limit(5)
                        .map(e -> new UserInterestDTO.InterestTag(e.getKey(), e.getValue()))
                        .collect(Collectors.toList());
                dto.setTopTags(topTags);
            } catch (JsonProcessingException e) {
                log.error("Failed to parse interest data for user {}", userId, e);
            }
        }
        
        return dto;
    }
    
    @Override
    public RadarPlaylistDTO getRadarPlaylist(Long userId) {
        Optional<RadarPlaylist> radarOpt = radarPlaylistRepository
                .findByUserIdAndExpiresAtAfter(userId, new Date());
        
        if (radarOpt.isEmpty()) {
            log.info("Radar playlist not found for user {}, generating...", userId);
            generateRadarPlaylist(userId);
            radarOpt = radarPlaylistRepository.findByUserIdAndExpiresAtAfter(userId, new Date());
        }
        
        RadarPlaylist radar = radarOpt.orElse(null);
        if (radar == null) {
            return null;
        }
        
        return convertToDTO(radar);
    }
    
    @Override
    @Transactional
    public void calculateUserInterestProfile(Long userId) {
        log.info("Calculating interest profile for user {}", userId);
        
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -RECENT_DAYS);
        Date startDate = cal.getTime();
        
        List<Object[]> songScores = userBehaviorRepository.calculateSongScores(userId, startDate);
        
        Map<String, Double> interestProfile = new HashMap<>();
        
        for (Object[] row : songScores) {
            Long songId = (Long) row[0];
            Integer totalScore = ((Number) row[1]).intValue();
            
            Optional<Song> songOpt = songRepository.findById(songId);
            if (songOpt.isPresent()) {
                Song song = songOpt.get();
                if (song.getCategory() != null && song.getCategory().getName() != null) {
                    String tag = song.getCategory().getName();
                    interestProfile.merge(tag, totalScore * 1.0, Double::sum);
                }
            }
        }
        
        saveUserInterestProfile(userId, interestProfile);
        log.info("Interest profile calculated for user {}: {}", userId, interestProfile);
    }
    
    @Override
    @Transactional
    public void generateRadarPlaylist(Long userId) {
        log.info("Generating radar playlist for user {}", userId);
        
        calculateUserInterestProfile(userId);
        
        Map<String, Double> profile = getUserInterests(userId);
        
        if (profile.isEmpty()) {
            log.warn("No interest profile for user {}, using default recommendations", userId);
            profile = getDefaultInterests();
        }
        
        List<Map.Entry<String, Double>> sorted = profile.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(3)
                .collect(Collectors.toList());
        
        List<Song> candidates = new ArrayList<>();
        for (Map.Entry<String, Double> entry : sorted) {
            String tag = entry.getKey();
            List<Song> songs = songRepository.findByCategoryNameOrderByPlayCountDesc(
                    tag, PageRequest.of(0, CATEGORY_SONG_COUNT));
            candidates.addAll(songs);
        }
        
        List<Song> finalSongs = candidates.stream()
                .distinct()
                .limit(RADAR_PLAYLIST_SIZE)
                .collect(Collectors.toList());
        
        List<Long> songIds = finalSongs.stream()
                .map(Song::getId)
                .collect(Collectors.toList());
        
        String topTag = sorted.isEmpty() ? "流行" : sorted.get(0).getKey();
        Map<String, Double> summaryMap = sorted.stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> a,
                        LinkedHashMap::new));
        
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        
        RadarPlaylist radar = new RadarPlaylist();
        radar.setUserId(userId);
        radar.setSongIds(toJson(songIds));
        radar.setInterestSummary(toJson(summaryMap));
        radar.setCreatedAt(new Date());
        radar.setExpiresAt(cal.getTime());
        
        radarPlaylistRepository.save(radar);
        log.info("Radar playlist generated for user {} with {} songs", userId, finalSongs.size());
    }
    
    @Override
    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional
    public void dailyRadarGeneration() {
        log.info("Starting daily radar playlist generation...");
        
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -30);
        Date thirtyDaysAgo = cal.getTime();
        
        List<User> activeUsers = userRepository.findActiveUsers(thirtyDaysAgo);
        log.info("Found {} active users for radar generation", activeUsers.size());
        
        int successCount = 0;
        int failCount = 0;
        
        for (User user : activeUsers) {
            try {
                generateRadarPlaylist(user.getId());
                successCount++;
            } catch (Exception e) {
                failCount++;
                log.error("Failed to generate radar playlist for user {}", user.getId(), e);
            }
        }
        
        radarPlaylistRepository.deleteExpired(new Date());
        
        log.info("Daily radar generation completed. Success: {}, Failed: {}", successCount, failCount);
    }
    
    private void saveUserInterestProfile(Long userId, Map<String, Double> interests) {
        Optional<UserInterestProfile> existing = userInterestProfileRepository.findByUserId(userId);
        
        UserInterestProfile profile;
        if (existing.isPresent()) {
            profile = existing.get();
        } else {
            profile = new UserInterestProfile();
            profile.setUserId(userId);
        }
        
        profile.setInterestData(toJson(interests));
        profile.setLastUpdated(new Date());
        
        userInterestProfileRepository.save(profile);
    }
    
    private Map<String, Double> getUserInterests(Long userId) {
        Optional<UserInterestProfile> profileOpt = userInterestProfileRepository.findByUserId(userId);
        
        if (profileOpt.isPresent()) {
            try {
                return objectMapper.readValue(
                        profileOpt.get().getInterestData(),
                        new TypeReference<Map<String, Double>>() {});
            } catch (JsonProcessingException e) {
                log.error("Failed to parse interest data", e);
            }
        }
        
        return new HashMap<>();
    }
    
    private Map<String, Double> getDefaultInterests() {
        Map<String, Double> defaults = new LinkedHashMap<>();
        defaults.put("流行", 10.0);
        defaults.put("摇滚", 8.0);
        defaults.put("民谣", 5.0);
        return defaults;
    }
    
    private RadarPlaylistDTO convertToDTO(RadarPlaylist radar) {
        RadarPlaylistDTO dto = new RadarPlaylistDTO();
        dto.setId(radar.getId());
        dto.setGeneratedAt(radar.getCreatedAt());
        dto.setExpiresAt(radar.getExpiresAt());
        
        try {
            List<Long> songIds = objectMapper.readValue(
                    radar.getSongIds(),
                    new TypeReference<List<Long>>() {});
            
            List<RadarPlaylistDTO.SongDTO> songs = new ArrayList<>();
            for (Long songId : songIds) {
                Optional<Song> songOpt = songRepository.findById(songId);
                if (songOpt.isPresent()) {
                    Song song = songOpt.get();
                    String artistName = song.getArtists() != null && !song.getArtists().isEmpty()
                            ? song.getArtists().iterator().next().getName()
                            : "未知艺术家";
                    songs.add(new RadarPlaylistDTO.SongDTO(
                            song.getId(),
                            song.getTitle(),
                            song.getCover(),
                            artistName,
                            song.getDuration()
                    ));
                }
            }
            dto.setSongs(songs);
            
            Map<String, Double> summary = objectMapper.readValue(
                    radar.getInterestSummary(),
                    new TypeReference<Map<String, Double>>() {});
            
            List<RadarPlaylistDTO.InterestTag> tags = summary.entrySet().stream()
                    .limit(5)
                    .map(e -> new RadarPlaylistDTO.InterestTag(e.getKey(), e.getValue()))
                    .collect(Collectors.toList());
            
            String topTag = tags.isEmpty() ? "流行" : tags.get(0).getName();
            dto.setInterestSummary(new RadarPlaylistDTO.InterestSummary(topTag, tags));
            
        } catch (JsonProcessingException e) {
            log.error("Failed to convert radar playlist to DTO", e);
        }
        
        return dto;
    }
    
    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize to JSON", e);
            return "{}";
        }
    }
}
