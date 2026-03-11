package com.example.flymusic.repository;

import com.example.flymusic.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 评论Repository
 * 提供评论相关的数据库操作
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    /**
     * 根据目标类型和目标ID查询评论
     */
    List<Comment> findByTargetTypeAndTargetIdOrderByCreatedAtDesc(Integer targetType, Long targetId);
    
    /**
     * 根据父评论ID查询回复
     */
    List<Comment> findByParentIdOrderByCreatedAtAsc(Long parentId);
    
    /**
     * 根据用户ID查询评论
     */
    List<Comment> findByUserId(Long userId);
    
    /**
     * 统计评论数量
     */
    long countByTargetTypeAndTargetId(Integer targetType, Long targetId);
    
    /**
     * 分页查询评论
     */
    Page<Comment> findByTargetTypeAndTargetId(Integer targetType, Long targetId, Pageable pageable);
}