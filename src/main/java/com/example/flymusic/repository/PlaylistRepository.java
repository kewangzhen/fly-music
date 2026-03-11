package com.example.flymusic.repository;

import com.example.flymusic.entity.Playlist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 播放列表Repository
 * 提供播放列表相关的数据库操作
 */
@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
    
    /**
     * 根据用户ID查询播放列表
     */
    List<Playlist> findByUserId(Long userId);
    
    /**
     * 查询公开的播放列表
     */
    List<Playlist> findByIsPublicTrue();
    
    /**
     * 根据名称搜索播放列表
     */
    List<Playlist> findByNameContaining(String name);
    
    /**
     * 根据类型查询播放列表
     */
    List<Playlist> findByType(Integer type);
    
    /**
     * 根据状态查询播放列表
     */
    List<Playlist> findByStatus(Integer status);
    
    /**
     * 查询热门播放列表
     */
    @Query("SELECT p FROM Playlist p WHERE p.isPublic = true AND p.status = 1 ORDER BY p.playCount DESC")
    List<Playlist> findPopularPlaylists(Pageable pageable);
    
    /**
     * 查询心动歌单（根据用户偏好推荐）
     */
    @Query("SELECT p FROM Playlist p WHERE p.isPublic = true AND p.status = 1 AND p.type = 1 ORDER BY p.playCount DESC")
    List<Playlist> findHeartPlaylists(Pageable pageable);
    
    /**
     * 查询雷达榜单
     */
    @Query("SELECT p FROM Playlist p WHERE p.isPublic = true AND p.status = 1 AND p.type = 2 ORDER BY p.playCount DESC")
    List<Playlist> findRadarPlaylists(Pageable pageable);
    
    /**
     * 统计播放列表总数
     */
    long countByStatus(Integer status);
    
    /**
     * 分页查询播放列表
     */
    Page<Playlist> findByStatus(Integer status, Pageable pageable);
}
