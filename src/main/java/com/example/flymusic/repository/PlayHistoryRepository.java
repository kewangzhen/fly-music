package com.example.flymusic.repository;

import com.example.flymusic.entity.PlayHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * 播放历史Repository
 * 提供播放历史相关的数据库操作
 */
@Repository
public interface PlayHistoryRepository extends JpaRepository<PlayHistory, Long> {
    
    /**
     * 根据用户ID查询播放历史
     */
    List<PlayHistory> findByUserIdOrderByPlayedAtDesc(Long userId);
    
    /**
     * 根据用户ID查询最近播放历史
     */
    @Query("SELECT ph FROM PlayHistory ph WHERE ph.user.id = :userId ORDER BY ph.playedAt DESC")
    List<PlayHistory> findRecentPlayHistory(@Param("userId") Long userId, org.springframework.data.domain.Pageable pageable);
    
    /**
     * 统计用户播放歌曲总数
     */
    @Query("SELECT COUNT(DISTINCT ph.song.id) FROM PlayHistory ph WHERE ph.user.id = :userId")
    long countDistinctSongsByUserId(@Param("userId") Long userId);
    
    /**
     * 统计用户播放总时长
     */
    @Query("SELECT SUM(ph.playDuration) FROM PlayHistory ph WHERE ph.user.id = :userId")
    Long sumPlayDurationByUserId(@Param("userId") Long userId);
    
    /**
     * 查询用户播放最多的歌曲
     */
    @Query("SELECT ph.song.id, COUNT(ph) as playCount FROM PlayHistory ph WHERE ph.user.id = :userId GROUP BY ph.song.id ORDER BY playCount DESC")
    List<Object[]> findMostPlayedSongsByUserId(@Param("userId") Long userId, org.springframework.data.domain.Pageable pageable);
    
    /**
     * 查询用户常听的歌手
     */
    @Query("SELECT ph.song.artists FROM PlayHistory ph WHERE ph.user.id = :userId GROUP BY ph.song.artists ORDER BY COUNT(ph) DESC")
    List<Object[]> findFavoriteArtistsByUserId(@Param("userId") Long userId, org.springframework.data.domain.Pageable pageable);
    
    /**
     * 统计指定时间范围内的播放次数
     */
    @Query("SELECT COUNT(ph) FROM PlayHistory ph WHERE ph.playedAt >= :startDate AND ph.playedAt <= :endDate")
    long countByPlayedAtBetween(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
    
    /**
     * 根据用户ID和时间范围查询播放历史
     */
    @Query("SELECT ph FROM PlayHistory ph WHERE ph.user.id = :userId AND ph.playedAt >= :startDate AND ph.playedAt <= :endDate ORDER BY ph.playedAt DESC")
    List<PlayHistory> findByUserIdAndPlayedAtBetween(@Param("userId") Long userId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);
    
    /**
     * 统计用户播放天数（去重）
     */
    @Query("SELECT COUNT(DISTINCT FUNCTION('DATE', ph.playedAt)) FROM PlayHistory ph WHERE ph.user.id = :userId AND ph.playedAt >= :startDate")
    long countDistinctDaysByUserId(@Param("userId") Long userId, @Param("startDate") Date startDate);
    
    /**
     * 查询用户按分类分组的播放次数
     */
    @Query("SELECT ph.song.category.id, COUNT(ph) FROM PlayHistory ph WHERE ph.user.id = :userId AND ph.playedAt >= :startDate AND ph.song.category IS NOT NULL GROUP BY ph.song.category.id")
    List<Object[]> countByCategory(@Param("userId") Long userId, @Param("startDate") Date startDate);
    
    /**
     * 查询用户按小时分组的播放次数
     */
    @Query("SELECT FUNCTION('HOUR', ph.playedAt), COUNT(ph) FROM PlayHistory ph WHERE ph.user.id = :userId AND ph.playedAt >= :startDate GROUP BY FUNCTION('HOUR', ph.playedAt)")
    List<Object[]> countByHour(@Param("userId") Long userId, @Param("startDate") Date startDate);

    /**
     * 统计某个分类的播放次数
     */
    @Query("SELECT COUNT(ph) FROM PlayHistory ph WHERE ph.song.category.id = :categoryId")
    long countBySongCategoryId(@Param("categoryId") Long categoryId);

    /**
     * 按歌手统计播放次数（全局）
     */
    @Query("SELECT a.id, a.name, a.avatar, COUNT(ph) as playCount " +
           "FROM PlayHistory ph JOIN ph.song.artists a " +
           "GROUP BY a.id, a.name, a.avatar " +
           "ORDER BY playCount DESC")
    List<Object[]> countByArtist(org.springframework.data.domain.Pageable pageable);

    /**
     * 统计时间范围内的播放时长
     */
    @Query("SELECT SUM(ph.playDuration) FROM PlayHistory ph WHERE ph.playedAt >= :startDate AND ph.playedAt <= :endDate")
    Long sumDurationByPlayedAtBetween(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    /**
     * 统计时间范围内的活跃用户数（去重）
     */
    @Query("SELECT COUNT(DISTINCT ph.user.id) FROM PlayHistory ph WHERE ph.playedAt >= :startDate AND ph.playedAt <= :endDate")
    long countDistinctUsersByPlayedAtBetween(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
}