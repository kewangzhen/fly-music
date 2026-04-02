package com.example.flymusic.service.impl;

import com.example.flymusic.entity.*;
import com.example.flymusic.entity.dto.HotRankingDTO;
import com.example.flymusic.entity.dto.HotSongDTO;
import com.example.flymusic.entity.enums.RankingType;
import com.example.flymusic.repository.*;
import com.example.flymusic.service.RankingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RankingServiceImpl implements RankingService {
    
    private static final Logger log = LoggerFactory.getLogger(RankingServiceImpl.class);
    
    private static final int WEIGHT_PLAY = 1;
    private static final int WEIGHT_FAVORITE = 5;
    private static final int WEIGHT_SHARE = 8;
    private static final int WEIGHT_DOWNLOAD = 3;
    private static final int WEIGHT_COMMENT = 4;
    private static final double GRAVITY = 1.8;
    private static final int RANKING_SIZE = 100;
    private static final int NEW_SONG_DAYS = 7;
    
    private static final int TARGET_TYPE_SONG = 1;
    
    @Autowired
    private SongRepository songRepository;
    
    @Autowired
    private SongHotScoreRepository songHotScoreRepository;
    
    @Autowired
    private HotRankingRepository hotRankingRepository;
    
    @Autowired
    private UserFavoriteRepository userFavoriteRepository;
    
    @Autowired
    private CommentRepository commentRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Override
    public HotRankingDTO getGlobalRanking() {
        List<HotRanking> rankings = hotRankingRepository
            .findGlobalByRankingType(RankingType.GLOBAL.getCode());
        
        HotRankingDTO dto = new HotRankingDTO(RankingType.GLOBAL.getCode(), "热门榜");
        
        if (!rankings.isEmpty()) {
            Date updateTime = rankings.get(0).getCreatedAt();
            dto.setUpdateTime(updateTime != null ? updateTime.toString() : new Date().toString());
            
            List<HotSongDTO> songs = buildHotSongsFromRankings(rankings);
            dto.setSongs(songs);
        } else {
            dto.setUpdateTime(new Date().toString());
            dto.setSongs(new ArrayList<>());
        }
        
        return dto;
    }
    
    @Override
    public HotRankingDTO getCategoryRanking(Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElse(null);
        String title = category != null ? category.getName() + "热门榜" : "分类热门榜";
        
        List<HotRanking> rankings = hotRankingRepository
            .findByRankingTypeAndCategory(RankingType.CATEGORY.getCode(), categoryId);
        
        HotRankingDTO dto = new HotRankingDTO(RankingType.CATEGORY.getCode(), title);
        
        if (!rankings.isEmpty()) {
            Date updateTime = rankings.get(0).getCreatedAt();
            dto.setUpdateTime(updateTime != null ? updateTime.toString() : new Date().toString());
            
            List<HotSongDTO> songs = buildHotSongsFromRankings(rankings);
            dto.setSongs(songs);
        } else {
            dto.setUpdateTime(new Date().toString());
            dto.setSongs(new ArrayList<>());
        }
        
        return dto;
    }
    
    @Override
    public HotRankingDTO getNewSongRanking() {
        List<HotRanking> rankings = hotRankingRepository
            .findGlobalByRankingType(RankingType.NEW.getCode());
        
        HotRankingDTO dto = new HotRankingDTO(RankingType.NEW.getCode(), "新歌榜");
        
        if (!rankings.isEmpty()) {
            Date updateTime = rankings.get(0).getCreatedAt();
            dto.setUpdateTime(updateTime != null ? updateTime.toString() : new Date().toString());
            
            List<HotSongDTO> songs = buildHotSongsFromRankings(rankings);
            dto.setSongs(songs);
        } else {
            dto.setUpdateTime(new Date().toString());
            dto.setSongs(new ArrayList<>());
        }
        
        return dto;
    }
    
    @Override
    public HotRankingDTO getSoaringRanking() {
        List<HotRanking> rankings = hotRankingRepository
            .findGlobalByRankingType(RankingType.SOARING.getCode());
        
        HotRankingDTO dto = new HotRankingDTO(RankingType.SOARING.getCode(), "飙升榜");
        
        if (!rankings.isEmpty()) {
            Date updateTime = rankings.get(0).getCreatedAt();
            dto.setUpdateTime(updateTime != null ? updateTime.toString() : new Date().toString());
            
            List<HotSongDTO> songs = buildHotSongsFromRankings(rankings);
            dto.setSongs(songs);
        } else {
            dto.setUpdateTime(new Date().toString());
            dto.setSongs(new ArrayList<>());
        }
        
        return dto;
    }
    
    @Override
    @Transactional
    public void calculateHotScores() {
        log.info("开始计算歌曲热度得分...");
        
        List<Song> songs = songRepository.findByStatus(1);
        
        for (Song song : songs) {
            Long songId = song.getId();
            
            long playCount = song.getPlayCount() != null ? song.getPlayCount() : 0;
            long favoriteCount = userFavoriteRepository.countByTargetTypeAndTargetId(TARGET_TYPE_SONG, songId);
            long commentCount = commentRepository.countByTargetTypeAndTargetId(TARGET_TYPE_SONG, songId);
            
            int playScore = (int) (playCount * WEIGHT_PLAY);
            int favoriteScore = (int) (favoriteCount * WEIGHT_FAVORITE);
            int shareScore = (int) (song.getShareCount() != null ? song.getShareCount() * WEIGHT_SHARE : 0);
            int downloadScore = (int) ((song.getDownloadCount() != null ? song.getDownloadCount() : 0) * WEIGHT_DOWNLOAD);
            int commentScore = (int) (commentCount * WEIGHT_COMMENT);
            
            long totalWeightedScore = playScore + favoriteScore + shareScore + downloadScore + commentScore;
            
            double hotScore = calculateHotScore(totalWeightedScore, song.getCreatedAt());
            
            Optional<SongHotScore> existingOpt = songHotScoreRepository.findBySongId(songId);
            SongHotScore hotScoreEntity;
            if (existingOpt.isPresent()) {
                hotScoreEntity = existingOpt.get();
            } else {
                hotScoreEntity = new SongHotScore();
                hotScoreEntity.setSongId(songId);
            }
            
            hotScoreEntity.setPlayScore(playScore);
            hotScoreEntity.setFavoriteScore(favoriteScore);
            hotScoreEntity.setShareScore(shareScore);
            hotScoreEntity.setDownloadScore(downloadScore);
            hotScoreEntity.setCommentScore(commentScore);
            hotScoreEntity.setTotalWeightedScore(totalWeightedScore);
            hotScoreEntity.setHotScore(hotScore);
            
            songHotScoreRepository.save(hotScoreEntity);
        }
        
        log.info("歌曲热度得分计算完成，共计算 {} 首歌", songs.size());
    }
    
    @Override
    @Transactional
    public void generateAllRankings() {
        log.info("开始生成热门榜单...");
        
        calculateHotScores();
        
        generateGlobalRanking();
        generateCategoryRankings();
        generateNewSongRanking();
        generateSoaringRanking();
        
        hotRankingRepository.deleteOldRankings(new Date());
        
        log.info("热门榜单生成完成");
    }
    
    @Scheduled(cron = "0 30 2 * * ?")
    @Transactional
    public void dailyCalculateHotScores() {
        log.info("每日定时任务：开始计算歌曲热度得分");
        calculateHotScores();
    }
    
    @Scheduled(cron = "0 0 3 * * ?")
    @Transactional
    public void dailyGenerateGlobalRanking() {
        log.info("每日定时任务：开始生成全局热门榜");
        generateGlobalRanking();
    }
    
    @Scheduled(cron = "0 30 3 * * ?")
    @Transactional
    public void dailyGenerateCategoryRankings() {
        log.info("每日定时任务：开始生成分类热门榜");
        generateCategoryRankings();
    }
    
    @Scheduled(cron = "0 0 4 * * ?")
    @Transactional
    public void dailyGenerateNewSongRanking() {
        log.info("每日定时任务：开始生成新歌榜");
        generateNewSongRanking();
    }
    
    @Scheduled(cron = "0 0 * * * ?")
    @Transactional
    public void hourlyUpdateSoaringRanking() {
        log.info("每小时定时任务：开始更新飙升榜");
        generateSoaringRanking();
    }
    
    private double calculateHotScore(long weightedScore, Date createdAt) {
        if (createdAt == null) {
            return weightedScore;
        }
        
        long daysSinceCreation = getDaysBetween(createdAt, new Date());
        double decayFactor = Math.pow(daysSinceCreation + 2, GRAVITY);
        return weightedScore / decayFactor;
    }
    
    private long getDaysBetween(Date start, Date end) {
        if (start == null || end == null) {
            return 0;
        }
        long diff = end.getTime() - start.getTime();
        return diff / (1000 * 60 * 60 * 24);
    }
    
    private void generateGlobalRanking() {
        log.info("生成全局热门榜...");
        
        hotRankingRepository.deleteByRankingType(RankingType.GLOBAL.getCode());
        
        List<SongHotScore> hotScores = songHotScoreRepository
            .findAllByOrderByHotScoreDesc(PageRequest.of(0, RANKING_SIZE));
        
        for (int i = 0; i < hotScores.size(); i++) {
            SongHotScore score = hotScores.get(i);
            saveRanking(RankingType.GLOBAL.getCode(), null, score.getSongId(), 
                       i + 1, score.getHotScore());
        }
        
        log.info("全局热门榜生成完成，共 {} 首", hotScores.size());
    }
    
    private void generateCategoryRankings() {
        log.info("生成分类热门榜...");
        
        hotRankingRepository.deleteByRankingType(RankingType.CATEGORY.getCode());
        
        List<Category> categories = categoryRepository.findAll();
        
        for (Category category : categories) {
            List<SongHotScore> hotScores = songHotScoreRepository
                .findByCategoryIdOrderByHotScoreDesc(category.getId(), PageRequest.of(0, RANKING_SIZE));
            
            for (int i = 0; i < hotScores.size(); i++) {
                SongHotScore score = hotScores.get(i);
                saveRanking(RankingType.CATEGORY.getCode(), category.getId(), score.getSongId(),
                           i + 1, score.getHotScore());
            }
        }
        
        log.info("分类热门榜生成完成，共 {} 个分类", categories.size());
    }
    
    private void generateNewSongRanking() {
        log.info("生成新歌榜...");
        
        hotRankingRepository.deleteByRankingType(RankingType.NEW.getCode());
        
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -NEW_SONG_DAYS);
        Date startDate = cal.getTime();
        
        List<SongHotScore> hotScores = songHotScoreRepository
            .findNewSongsOrderByHotScoreDesc(startDate, PageRequest.of(0, RANKING_SIZE));
        
        for (int i = 0; i < hotScores.size(); i++) {
            SongHotScore score = hotScores.get(i);
            saveRanking(RankingType.NEW.getCode(), null, score.getSongId(),
                       i + 1, score.getHotScore());
        }
        
        log.info("新歌榜生成完成，共 {} 首", hotScores.size());
    }
    
    private void generateSoaringRanking() {
        log.info("生成飙升榜...");
        
        hotRankingRepository.deleteByRankingType(RankingType.SOARING.getCode());
        
        List<SongHotScore> hotScores = songHotScoreRepository
            .findAllByOrderByHotScoreDesc(PageRequest.of(0, RANKING_SIZE));
        
        for (int i = 0; i < hotScores.size(); i++) {
            SongHotScore score = hotScores.get(i);
            saveRanking(RankingType.SOARING.getCode(), null, score.getSongId(),
                       i + 1, score.getHotScore());
        }
        
        log.info("飙升榜生成完成");
    }
    
    private void saveRanking(String type, Long categoryId, Long songId, int rank, Double hotScore) {
        HotRanking ranking = new HotRanking();
        ranking.setRankingType(type);
        ranking.setCategoryId(categoryId);
        ranking.setSongId(songId);
        ranking.setRank(rank);
        ranking.setHotScore(hotScore);
        ranking.setCreatedAt(new Date());
        
        hotRankingRepository.save(ranking);
    }
    
    private List<HotSongDTO> buildHotSongsFromRankings(List<HotRanking> rankings) {
        return rankings.stream()
            .map(r -> buildHotSongDTO(r.getSongId(), r.getRank(), r.getHotScore()))
            .collect(Collectors.toList());
    }
    
    private HotSongDTO buildHotSongDTO(Long songId, int rank, Double hotScore) {
        Optional<Song> songOpt = songRepository.findById(songId);
        
        if (songOpt.isEmpty()) {
            return new HotSongDTO(rank, songId, "未知歌曲", "", "未知艺术家", 0L, hotScore);
        }
        
        Song song = songOpt.get();
        String artistName = getArtistNames(song);
        
        return new HotSongDTO(
            rank,
            song.getId(),
            song.getTitle(),
            song.getCover() != null ? song.getCover() : "",
            artistName,
            song.getPlayCount() != null ? song.getPlayCount() : 0L,
            hotScore
        );
    }
    
    private String getArtistNames(Song song) {
        if (song.getArtists() != null && !song.getArtists().isEmpty()) {
            return song.getArtists().stream()
                .map(Artist::getName)
                .collect(Collectors.joining(", "));
        }
        return "未知艺术家";
    }
}
