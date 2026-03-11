package com.example.flymusic.repository;

import com.example.flymusic.entity.SystemLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * 系统日志Repository
 * 提供系统日志相关的数据库操作
 */
@Repository
public interface SystemLogRepository extends JpaRepository<SystemLog, Long> {
    
    /**
     * 根据用户ID查询日志
     */
    List<SystemLog> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    /**
     * 根据操作类型查询日志
     */
    List<SystemLog> findByAction(String action);
    
    /**
     * 根据目标类型和目标ID查询日志
     */
    List<SystemLog> findByTargetTypeAndTargetId(String targetType, Long targetId);
    
    /**
     * 查询指定时间范围内的日志
     */
    @Query("SELECT sl FROM SystemLog sl WHERE sl.createdAt >= :startDate AND sl.createdAt <= :endDate ORDER BY sl.createdAt DESC")
    List<SystemLog> findByCreatedAtBetween(@Param("startDate") Date startDate, @Param("endDate") Date endDate, Pageable pageable);
    
    /**
     * 统计操作次数
     */
    long countByAction(String action);
    
    /**
     * 分页查询日志
     */
    Page<SystemLog> findByUserId(Long userId, Pageable pageable);
}