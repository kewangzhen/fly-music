package com.example.flymusic.repository;

import com.example.flymusic.entity.Artist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 艺术家Repository
 * 提供艺术家相关的数据库操作
 */
@Repository
public interface ArtistRepository extends JpaRepository<Artist, Long> {
    
    /**
     * 根据名称搜索艺术家
     */
    List<Artist> findByNameContaining(String name);
    
    /**
     * 根据状态查询艺术家
     */
    List<Artist> findByStatus(Integer status);
    
    /**
     * 统计艺术家数量
     */
    long countByStatus(Integer status);
    
    /**
     * 分页查询艺术家
     */
    Page<Artist> findByStatus(Integer status, Pageable pageable);
}