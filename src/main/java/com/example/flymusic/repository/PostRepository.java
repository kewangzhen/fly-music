package com.example.flymusic.repository;

import com.example.flymusic.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 动态帖子Repository
 * 提供动态帖子相关的数据库操作
 */
@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    
    /**
     * 根据用户ID查询动态
     */
    List<Post> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    /**
     * 根据状态查询动态
     */
    List<Post> findByStatus(Integer status);
    
    /**
     * 查询关注用户的动态
     */
    @Query("SELECT p FROM Post p WHERE p.user.id IN " +
           "(SELECT uf.followed.id FROM UserFollow uf WHERE uf.follower.id = :userId) " +
           "AND p.status = 1 ORDER BY p.createdAt DESC")
    List<Post> findFollowingPosts(@Param("userId") Long userId, Pageable pageable);
    
    /**
     * 查询热门动态
     */
    @Query("SELECT p FROM Post p WHERE p.status = 1 ORDER BY p.likes DESC, p.commentsCount DESC")
    List<Post> findPopularPosts(Pageable pageable);
    
    /**
     * 统计动态数量
     */
    long countByUserId(Long userId);
    
    /**
     * 分页查询动态
     */
    Page<Post> findByStatus(Integer status, Pageable pageable);
}