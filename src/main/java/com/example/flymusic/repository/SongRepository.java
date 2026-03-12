package com.example.flymusic.repository;

import com.example.flymusic.entity.Song;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 歌曲Repository
 * 提供歌曲相关的数据库操作
 */
@Repository
public interface SongRepository extends JpaRepository<Song, Long> {
    
    /**
     * 根据标题搜索歌曲
     */
    List<Song> findByTitleContaining(String title);
    
    /**
     * 根据分类ID查询歌曲
     */
    List<Song> findByCategoryId(Long categoryId);
    
    /**
     * 根据分类ID查询歌曲（按播放量排序）
     */
    @Query("SELECT s FROM Song s WHERE s.category.id = :categoryId AND s.status = 1 ORDER BY s.playCount DESC")
    List<Song> findByCategoryIdOrderByPlayCountDesc(@Param("categoryId") Long categoryId, Pageable pageable);
    
    /**
     * 根据专辑ID查询歌曲
     */
    List<Song> findByAlbumId(Long albumId);
    
    /**
     * 根据用户ID查询原创歌曲
     */
    List<Song> findByUserId(Long userId);
    
    /**
     * 根据状态查询歌曲
     */
    List<Song> findByStatus(Integer status);
    
    /**
     * 根据歌手ID查询歌曲
     */
    @Query("SELECT s FROM Song s JOIN s.artists a WHERE a.id = :artistId")
    List<Song> findByArtistId(@Param("artistId") Long artistId);
    
    /**
     * 搜索歌曲（标题或歌手名）
     */
    @Query("SELECT s FROM Song s JOIN s.artists a WHERE s.title LIKE %:keyword% OR a.name LIKE %:keyword%")
    List<Song> searchSongs(@Param("keyword") String keyword);
    
    /**
     * 查询热门歌曲
     */
    @Query("SELECT s FROM Song s WHERE s.status = 1 ORDER BY s.playCount DESC")
    List<Song> findPopularSongs(Pageable pageable);
    
    /**
     * 查询最新歌曲
     */
    @Query("SELECT s FROM Song s WHERE s.status = 1 ORDER BY s.createdAt DESC")
    List<Song> findLatestSongs(Pageable pageable);
    
    /**
     * 根据用户播放历史推荐歌曲
     */
    @Query("SELECT s FROM Song s WHERE s.id NOT IN " +
           "(SELECT ph.song.id FROM PlayHistory ph WHERE ph.user.id = :userId) " +
           "AND s.status = 1 ORDER BY s.playCount DESC")
    List<Song> findRecommendedSongs(@Param("userId") Long userId, Pageable pageable);
    
    /**
     * 统计歌曲播放次数
     */
    @Query("SELECT SUM(s.playCount) FROM Song s")
    Long sumAllPlayCount();
    
    /**
     * 统计歌曲总数
     */
    long countByStatus(Integer status);
    
    /**
     * 分页查询歌曲
     */
    Page<Song> findByStatus(Integer status, Pageable pageable);
    
    /**
     * 根据分类名称查询歌曲（按播放量排序）
     */
    @Query("SELECT s FROM Song s WHERE s.category.name = :categoryName AND s.status = 1 ORDER BY s.playCount DESC")
    List<Song> findByCategoryNameOrderByPlayCountDesc(@Param("categoryName") String categoryName, Pageable pageable);
}