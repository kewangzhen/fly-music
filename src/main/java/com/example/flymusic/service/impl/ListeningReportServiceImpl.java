package com.example.flymusic.service.impl;

import com.example.flymusic.entity.*;
import com.example.flymusic.entity.dto.ListeningReportDTO;
import com.example.flymusic.repository.*;
import com.example.flymusic.service.ListeningReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 听歌报告服务实现类
 * 实现用户听歌报告的生成和查询功能
 * 
 * 实现逻辑：
 * 1. 查询用户播放历史数据
 * 2. 统计各项指标
 * 3. 生成报告DTO
 * 
 * @author fly-music
 * @since 2026-03-12
 */
@Service
public class ListeningReportServiceImpl implements ListeningReportService {
    
    private static final Logger log = LoggerFactory.getLogger(ListeningReportServiceImpl.class);
    
    @Autowired
    private PlayHistoryRepository playHistoryRepository;
    
    @Autowired
    private SongRepository songRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private ArtistRepository artistRepository;
    
    /**
     * 获取当前年份的听歌报告
     * 
     * @param userId 用户ID
     * @return 听歌报告DTO
     */
    @Override
    public ListeningReportDTO getListeningReport(Long userId) {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        return generateReport(userId, currentYear);
    }
    
    /**
     * 获取指定年份的听歌报告
     * 
     * @param userId 用户ID
     * @param year 年份
     * @return 听歌报告DTO
     */
    @Override
    public ListeningReportDTO getListeningReportByYear(Long userId, Integer year) {
        return generateReport(userId, year);
    }
    
    /**
     * 获取简要统计信息
     * 
     * @param userId 用户ID
     * @return 听歌报告DTO（仅包含summary）
     */
    @Override
    public ListeningReportDTO getSummary(Long userId) {
        ListeningReportDTO dto = new ListeningReportDTO();
        dto.setUserId(userId);
        dto.setYear(Calendar.getInstance().get(Calendar.YEAR));
        
        // 获取本年开始时间
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        Date startDate = cal.getTime();
        
        // 统计播放次数
        long totalPlays = playHistoryRepository.countByPlayedAtBetween(startDate, new Date());
        
        // 统计听歌曲数
        long totalSongs = playHistoryRepository.countDistinctSongsByUserId(userId);
        
        // 统计播放时长（秒转换为小时）
        Long totalDuration = playHistoryRepository.sumPlayDurationByUserId(userId);
        double totalHours = totalDuration != null ? totalDuration / 3600.0 : 0.0;
        
        // 统计听歌天数
        long totalDays = playHistoryRepository.countDistinctDaysByUserId(userId, startDate);
        
        // 构建Summary
        ListeningReportDTO.Summary summary = new ListeningReportDTO.Summary();
        summary.setTotalSongs((int) totalSongs);
        summary.setTotalHours(Math.round(totalHours * 10) / 10.0);
        summary.setTotalDays((int) totalDays);
        summary.setTotalPlays(totalPlays);
        
        dto.setSummary(summary);
        
        return dto;
    }
    
    /**
     * 生成听歌报告
     * 
     * 实现步骤：
     * 1. 计算年度时间范围
     * 2. 统计基础数据
     * 3. 统计分类分布
     * 4. 计算TOP歌曲
     * 5. 计算TOP歌手
     * 6. 统计标签分布
     * 7. 统计时间分布
     * 
     * @param userId 用户ID
     * @param year 年份
     * @return 听歌报告DTO
     */
    private ListeningReportDTO generateReport(Long userId, Integer year) {
        log.info("开始生成用户{}的{}年听歌报告", userId, year);
        
        ListeningReportDTO dto = new ListeningReportDTO();
        dto.setUserId(userId);
        dto.setYear(year);
        
        // 获取年度时间范围
        Date[] dateRange = getYearDateRange(year);
        Date startDate = dateRange[0];
        Date endDate = dateRange[1];
        
        // 1. 统计基础数据
        ListeningReportDTO.Summary summary = calculateSummary(userId, startDate, endDate);
        dto.setSummary(summary);
        
        // 2. 统计分类分布（饼图）
        List<ListeningReportDTO.CategoryDistribution> categoryDistribution = calculateCategoryDistribution(userId, startDate);
        dto.setCategoryDistribution(categoryDistribution);
        
        // 3. 计算TOP歌曲
        List<ListeningReportDTO.TopSong> topSongs = calculateTopSongs(userId, startDate, 10);
        dto.setTopSongs(topSongs);
        
        // 4. 计算TOP歌手
        List<ListeningReportDTO.TopArtist> topArtists = calculateTopArtists(userId, startDate, 10);
        dto.setTopArtists(topArtists);
        
        // 5. 统计标签分布（与分类分布相同）
        Map<String, Integer> tagDistribution = categoryDistribution.stream()
            .collect(Collectors.toMap(
                ListeningReportDTO.CategoryDistribution::getName,
                ListeningReportDTO.CategoryDistribution::getValue,
                (a, b) -> a,
                LinkedHashMap::new
            ));
        dto.setTagDistribution(tagDistribution);
        
        // 6. 统计时间分布
        Map<String, Integer> timeDistribution = calculateTimeDistribution(userId, startDate);
        dto.setTimeDistribution(timeDistribution);
        
        // 设置生成时间
        dto.setGeneratedAt(new Date().toString());
        
        log.info("用户{}的听歌报告生成完成，共听{}首歌，{}小时",
            userId, summary.getTotalSongs(), summary.getTotalHours());
        
        return dto;
    }
    
    /**
     * 获取年度时间范围
     * 
     * @param year 年份
     * @return [开始日期, 结束日期]
     */
    private Date[] getYearDateRange(Integer year) {
        Calendar cal = Calendar.getInstance();
        
        // 开始日期：当年1月1日
        cal.set(year, Calendar.JANUARY, 1, 0, 0, 0);
        Date startDate = cal.getTime();
        
        // 结束日期：当年12月31日 23:59:59
        cal.set(year, Calendar.DECEMBER, 31, 23, 59, 59);
        Date endDate = cal.getTime();
        
        return new Date[]{startDate, endDate};
    }
    
    /**
     * 统计基础数据
     * 
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 基础统计对象
     */
    private ListeningReportDTO.Summary calculateSummary(Long userId, Date startDate, Date endDate) {
        // 统计播放次数
        long totalPlays = playHistoryRepository.countByPlayedAtBetween(startDate, endDate);
        
        // 统计听歌曲数（去重）
        List<PlayHistory> histories = playHistoryRepository.findByUserIdAndPlayedAtBetween(userId, startDate, endDate);
        Set<Long> songIds = histories.stream()
            .map(h -> h.getSong().getId())
            .collect(Collectors.toSet());
        int totalSongs = songIds.size();
        
        // 统计播放时长（秒转换为小时）
        double totalHours = histories.stream()
            .mapToLong(h -> h.getPlayDuration() != null ? h.getPlayDuration() : 0)
            .sum() / 3600.0;
        
        // 统计听歌天数（去重）
        Set<String> playDates = histories.stream()
            .map(h -> {
                Calendar cal = Calendar.getInstance();
                cal.setTime(h.getPlayedAt());
                return cal.get(Calendar.YEAR) + "-" + cal.get(Calendar.MONTH) + "-" + cal.get(Calendar.DAY_OF_MONTH);
            })
            .collect(Collectors.toSet());
        int totalDays = playDates.size();
        
        // 构建Summary
        ListeningReportDTO.Summary summary = new ListeningReportDTO.Summary();
        summary.setTotalSongs(totalSongs);
        summary.setTotalHours(Math.round(totalHours * 10) / 10.0);
        summary.setTotalDays(totalDays);
        summary.setTotalPlays(totalPlays);
        
        return summary;
    }
    
    /**
     * 统计分类分布（饼图数据）
     * 
     * @param userId 用户ID
     * @param startDate 开始日期
     * @return 分类分布列表
     */
    private List<ListeningReportDTO.CategoryDistribution> calculateCategoryDistribution(Long userId, Date startDate) {
        // 查询播放历史
        List<PlayHistory> histories = playHistoryRepository.findByUserIdOrderByPlayedAtDesc(userId);
        
        // 按分类分组统计
        Map<String, Long> categoryCount = new LinkedHashMap<>();
        for (PlayHistory history : histories) {
            Song song = history.getSong();
            if (song != null && song.getCategory() != null) {
                String categoryName = song.getCategory().getName();
                categoryCount.merge(categoryName, 1L, Long::sum);
            }
        }
        
        // 计算总数
        long total = categoryCount.values().stream().mapToLong(Long::longValue).sum();
        if (total == 0) {
            return new ArrayList<>();
        }
        
        // 转换为百分比
        List<ListeningReportDTO.CategoryDistribution> result = categoryCount.entrySet().stream()
            .map(e -> {
                int percentage = (int) (e.getValue() * 100 / total);
                return new ListeningReportDTO.CategoryDistribution(e.getKey(), percentage);
            })
            .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
            .collect(Collectors.toList());
        
        return result;
    }
    
    /**
     * 计算TOP歌曲
     * 
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param limit 返回数量
     * @return TOP歌曲列表
     */
    private List<ListeningReportDTO.TopSong> calculateTopSongs(Long userId, Date startDate, int limit) {
        // 查询播放历史
        List<PlayHistory> histories = playHistoryRepository.findByUserIdOrderByPlayedAtDesc(userId);
        
        // 按歌曲ID分组统计播放次数
        Map<Long, Long> songPlayCount = new HashMap<>();
        for (PlayHistory history : histories) {
            Long songId = history.getSong().getId();
            songPlayCount.merge(songId, 1L, Long::sum);
        }
        
        // 排序取前N个
        List<Map.Entry<Long, Long>> sorted = songPlayCount.entrySet().stream()
            .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
            .limit(limit)
            .collect(Collectors.toList());
        
        // 构建TOP歌曲列表
        List<ListeningReportDTO.TopSong> result = new ArrayList<>();
        for (int i = 0; i < sorted.size(); i++) {
            Long songId = sorted.get(i).getKey();
            Long playCount = sorted.get(i).getValue();
            
            Optional<Song> songOpt = songRepository.findById(songId);
            if (songOpt.isPresent()) {
                Song song = songOpt.get();
                String artistName = getArtistNames(song);
                
                result.add(new ListeningReportDTO.TopSong(
                    i + 1,
                    song.getId(),
                    song.getTitle(),
                    artistName,
                    song.getCover(),
                    playCount
                ));
            }
        }
        
        return result;
    }
    
    /**
     * 计算TOP歌手
     * 
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param limit 返回数量
     * @return TOP歌手列表
     */
    private List<ListeningReportDTO.TopArtist> calculateTopArtists(Long userId, Date startDate, int limit) {
        // 查询播放历史
        List<PlayHistory> histories = playHistoryRepository.findByUserIdOrderByPlayedAtDesc(userId);
        
        // 按歌手分组统计
        Map<Long, ArtistInfo> artistInfoMap = new HashMap<>();
        for (PlayHistory history : histories) {
            Song song = history.getSong();
            if (song != null && song.getArtists() != null) {
                for (Artist artist : song.getArtists()) {
                    ArtistInfo info = artistInfoMap.computeIfAbsent(artist.getId(), 
                        k -> new ArtistInfo(artist.getId(), artist.getName(), artist.getAvatar()));
                    info.incrementSongCount();
                    info.incrementPlayCount();
                }
            }
        }
        
        // 排序取前N个
        List<ArtistInfo> sorted = artistInfoMap.values().stream()
            .sorted((a, b) -> Long.compare(b.playCount, a.playCount))
            .limit(limit)
            .collect(Collectors.toList());
        
        // 构建TOP歌手列表
        List<ListeningReportDTO.TopArtist> result = new ArrayList<>();
        for (int i = 0; i < sorted.size(); i++) {
            ArtistInfo info = sorted.get(i);
            result.add(new ListeningReportDTO.TopArtist(
                i + 1,
                info.artistId,
                info.name,
                info.avatar,
                info.songCount,
                info.playCount
            ));
        }
        
        return result;
    }
    
    /**
     * 统计时间分布
     * 
     * @param userId 用户ID
     * @param startDate 开始日期
     * @return 时间分布Map
     */
    private Map<String, Integer> calculateTimeDistribution(Long userId, Date startDate) {
        // 查询播放历史
        List<PlayHistory> histories = playHistoryRepository.findByUserIdOrderByPlayedAtDesc(userId);
        
        // 按小时分组统计
        int[] hourCounts = new int[24];
        for (PlayHistory history : histories) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(history.getPlayedAt());
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            hourCounts[hour]++;
        }
        
        // 合并为4个时间段
        Map<String, Integer> result = new LinkedHashMap<>();
        result.put("凌晨", hourCounts[0] + hourCounts[1] + hourCounts[2] + hourCounts[3] + hourCounts[4] + hourCounts[5]);
        result.put("上午", hourCounts[6] + hourCounts[7] + hourCounts[8] + hourCounts[9] + hourCounts[10] + hourCounts[11]);
        result.put("下午", hourCounts[12] + hourCounts[13] + hourCounts[14] + hourCounts[15] + hourCounts[16] + hourCounts[17]);
        result.put("晚上", hourCounts[18] + hourCounts[19] + hourCounts[20] + hourCounts[21] + hourCounts[22] + hourCounts[23]);
        
        return result;
    }
    
    /**
     * 获取歌曲的艺术家名称
     */
    private String getArtistNames(Song song) {
        if (song.getArtists() != null && !song.getArtists().isEmpty()) {
            return song.getArtists().stream()
                .map(Artist::getName)
                .collect(Collectors.joining(", "));
        }
        return "未知艺术家";
    }
    
    /**
     * 歌手信息内部类
     */
    private static class ArtistInfo {
        Long artistId;
        String name;
        String avatar;
        int songCount;
        long playCount;
        
        ArtistInfo(Long artistId, String name, String avatar) {
            this.artistId = artistId;
            this.name = name;
            this.avatar = avatar;
            this.songCount = 0;
            this.playCount = 0;
        }
        
        void incrementSongCount() {
            this.songCount++;
        }
        
        void incrementPlayCount() {
            this.playCount++;
        }
    }
}
