package com.example.flymusic.repository;

import com.example.flymusic.entity.Album;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 专辑Repository
 * 提供专辑相关的数据库操作
 */
@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {
    
    /**
     * 根据标题搜索专辑
     */
    List<Album> findByTitleContaining(String title);
    
    /**
     * 根据名称搜索专辑
     */
    List<Album> findByNameContaining(String name);
    
    /**
     * 根据艺术家ID查询专辑
     */
    List<Album> findByArtistId(Long artistId);
    
    /**
     * 根据状态查询专辑
     */
    List<Album> findByStatus(Integer status);
    
    /**
     * 查询最新专辑
     */
    @Query("SELECT a FROM Album a WHERE a.status = 1 ORDER BY a.releaseDate DESC")
    List<Album> findLatestAlbums(Pageable pageable);
    
    /**
     * 查询热门专辑
     */
    @Query("SELECT a FROM Album a WHERE a.status = 1 ORDER BY a.playCount DESC")
    List<Album> findPopularAlbums(Pageable pageable);
    
    /**
     * 统计专辑数量
     */
    long countByStatus(Integer status);
    
    /**
     * 分页查询专辑
     */
    Page<Album> findByStatus(Integer status, Pageable pageable);
}