package com.example.flymusic.repository;

import com.example.flymusic.entity.UserFollow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 用户关注Repository
 * 提供用户关注相关的数据库操作
 */
@Repository
public interface UserFollowRepository extends JpaRepository<UserFollow, Long> {
    
    /**
     * 查询用户的粉丝
     */
    @Query("SELECT uf FROM UserFollow uf WHERE uf.followed.id = :userId")
    List<UserFollow> findFollowers(@Param("userId") Long userId);
    
    /**
     * 查询用户关注的人
     */
    @Query("SELECT uf FROM UserFollow uf WHERE uf.follower.id = :userId")
    List<UserFollow> findFollowing(@Param("userId") Long userId);
    
    /**
     * 检查是否已关注
     */
    boolean existsByFollowerIdAndFollowedId(Long followerId, Long followedId);
    
    /**
     * 查找关注关系
     */
    Optional<UserFollow> findByFollowerIdAndFollowedId(Long followerId, Long followedId);
    
    /**
     * 统计粉丝数量
     */
    long countByFollowedId(Long userId);
    
    /**
     * 统计关注数量
     */
    long countByFollowerId(Long userId);
}