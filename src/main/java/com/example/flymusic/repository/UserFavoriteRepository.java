package com.example.flymusic.repository;

import com.example.flymusic.entity.UserFavorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 用户收藏Repository
 * 提供用户收藏相关的数据库操作
 */
@Repository
public interface UserFavoriteRepository extends JpaRepository<UserFavorite, Long> {
    
    /**
     * 根据用户ID查询收藏
     */
    List<UserFavorite> findByUserId(Long userId);
    
    /**
     * 根据用户ID和目标类型查询收藏
     */
    List<UserFavorite> findByUserIdAndTargetType(Long userId, Integer targetType);
    
    /**
     * 检查是否已收藏
     */
    boolean existsByUserIdAndTargetTypeAndTargetId(Long userId, Integer targetType, Long targetId);
    
    /**
     * 查找特定收藏
     */
    Optional<UserFavorite> findByUserIdAndTargetTypeAndTargetId(Long userId, Integer targetType, Long targetId);
    
    /**
     * 统计用户收藏数量
     */
    long countByUserIdAndTargetType(Long userId, Integer targetType);
    
    /**
     * 统计用户收藏总数
     */
    long countByUserId(Long userId);
}