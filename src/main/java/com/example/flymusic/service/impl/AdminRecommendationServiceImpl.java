package com.example.flymusic.service.impl;

import com.example.flymusic.entity.Artist;
import com.example.flymusic.entity.Category;
import com.example.flymusic.entity.Song;
import com.example.flymusic.entity.User;
import com.example.flymusic.entity.dto.AdminRecommendationDTO;
import com.example.flymusic.repository.*;
import com.example.flymusic.service.AdminRecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class AdminRecommendationServiceImpl implements AdminRecommendationService {

    @Autowired
    private PlayHistoryRepository playHistoryRepository;

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ArtistRepository artistRepository;

    private static final Map<String, Integer> recommendationWeights = new ConcurrentHashMap<>();

    static {
        recommendationWeights.put("radar", 3);
        recommendationWeights.put("hot", 4);
        recommendationWeights.put("category", 2);
        recommendationWeights.put("similar", 1);
    }

    @Override
    public AdminRecommendationDTO getRecommendationData() {
        AdminRecommendationDTO dto = new AdminRecommendationDTO();

        dto.setCategoryDistribution(getCategoryDistribution());
        dto.setTopArtists(getTopArtists());
        dto.setActivityStats(getActivityStats());
        dto.setConfig(getConfig());

        return dto;
    }

    private AdminRecommendationDTO.CategoryDistribution getCategoryDistribution() {
        AdminRecommendationDTO.CategoryDistribution distribution = new AdminRecommendationDTO.CategoryDistribution();
        List<AdminRecommendationDTO.CategoryDistribution.CategoryItem> items = new ArrayList<>();

        List<Category> categories = categoryRepository.findAll();
        long totalPlays = playHistoryRepository.count();

        for (Category category : categories) {
            long categoryPlays = playHistoryRepository.countBySongCategoryId(category.getId());

            AdminRecommendationDTO.CategoryDistribution.CategoryItem item =
                new AdminRecommendationDTO.CategoryDistribution.CategoryItem();
            item.setName(category.getName());
            item.setPlayCount(categoryPlays);
            item.setPercentage(totalPlays > 0 ? (int) ((categoryPlays * 100.0) / totalPlays) : 0);
            items.add(item);
        }

        items.sort((a, b) -> Long.compare(b.getPlayCount(), a.getPlayCount()));
        distribution.setCategories(items);

        return distribution;
    }

    private List<AdminRecommendationDTO.TopArtist> getTopArtists() {
        List<Object[]> results = playHistoryRepository.countByArtist(PageRequest.of(0, 5));
        List<AdminRecommendationDTO.TopArtist> topArtists = new ArrayList<>();

        int rank = 1;
        for (Object[] row : results) {
            if (rank > 5) break;

            AdminRecommendationDTO.TopArtist artist = new AdminRecommendationDTO.TopArtist();
            artist.setArtistId((Long) row[0]);
            artist.setName((String) row[1]);
            artist.setAvatar((String) row[2]);
            artist.setPlayCount((Long) row[3]);
            artist.setRank(rank++);

            topArtists.add(artist);
        }

        return topArtists;
    }

    private AdminRecommendationDTO.ActivityStats getActivityStats() {
        AdminRecommendationDTO.ActivityStats stats = new AdminRecommendationDTO.ActivityStats();

        Date today = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date todayStart = calendar.getTime();

        long todayPlays = playHistoryRepository.countByPlayedAtBetween(todayStart, today);
        stats.setTodayPlays(todayPlays);

        Long todayDuration = playHistoryRepository.sumDurationByPlayedAtBetween(todayStart, today);
        stats.setTodayHours(todayDuration != null ? todayDuration / 3600.0 : 0.0);

        long activeUsers = playHistoryRepository.countDistinctUsersByPlayedAtBetween(todayStart, today);
        stats.setActiveUsers((int) activeUsers);

        stats.setTotalSongs((long) songRepository.count());
        stats.setTotalUsers((int) userRepository.count());

        return stats;
    }

    @Override
    public AdminRecommendationDTO.RecommendationConfig getConfig() {
        AdminRecommendationDTO.RecommendationConfig config = new AdminRecommendationDTO.RecommendationConfig();
        config.setRadarWeight(recommendationWeights.getOrDefault("radar", 3));
        config.setHotWeight(recommendationWeights.getOrDefault("hot", 4));
        config.setCategoryWeight(recommendationWeights.getOrDefault("category", 2));
        config.setSimilarWeight(recommendationWeights.getOrDefault("similar", 1));
        return config;
    }

    @Override
    public void updateConfig(AdminRecommendationDTO.RecommendationConfig config) {
        if (config.getRadarWeight() != null) {
            recommendationWeights.put("radar", Math.max(1, Math.min(5, config.getRadarWeight())));
        }
        if (config.getHotWeight() != null) {
            recommendationWeights.put("hot", Math.max(1, Math.min(5, config.getHotWeight())));
        }
        if (config.getCategoryWeight() != null) {
            recommendationWeights.put("category", Math.max(1, Math.min(5, config.getCategoryWeight())));
        }
        if (config.getSimilarWeight() != null) {
            recommendationWeights.put("similar", Math.max(1, Math.min(5, config.getSimilarWeight())));
        }
    }

    @Override
    public void recalculateWithNewWeights() {
        System.out.println("使用新权重重新计算推荐: " + recommendationWeights);
    }

    public static Map<String, Integer> getRecommendationWeights() {
        return Collections.unmodifiableMap(recommendationWeights);
    }
}
