package com.example.flymusic.repository;

import com.example.flymusic.entity.AiMusicGeneration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * AI音乐生成Repository
 * 提供AI音乐生成相关的数据库操作
 */
@Repository
public interface AiMusicGenerationRepository extends JpaRepository<AiMusicGeneration, Long> {
    
    /**
     * 根据用户ID查询AI生成记录
     */
    List<AiMusicGeneration> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    /**
     * 根据状态查询AI生成记录
     */
    List<AiMusicGeneration> findByStatus(Integer status);
    
    /**
     * 统计用户AI生成数量
     */
    long countByUserId(Long userId);
    
    /**
     * 分页查询AI生成记录
     */
    Page<AiMusicGeneration> findByUserId(Long userId, Pageable pageable);
}