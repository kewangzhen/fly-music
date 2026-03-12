package com.example.flymusic.repository;

import com.example.flymusic.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 用户Repository
 * 提供用户相关的数据库操作
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * 根据用户名查找用户
     */
    Optional<User> findByUsername(String username);
    
    /**
     * 根据邮箱查找用户
     */
    Optional<User> findByEmail(String email);
    
    /**
     * 根据手机号查找用户
     */
    Optional<User> findByPhone(String phone);
    
    /**
     * 根据重置令牌查找用户
     */
    Optional<User> findByResetToken(String resetToken);
    
    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 检查邮箱是否存在
     */
    boolean existsByEmail(String email);
    
    /**
     * 检查手机号是否存在
     */
    boolean existsByPhone(String phone);
    
    /**
     * 根据角色查找用户
     */
    List<User> findByRole(Integer role);
    
    /**
     * 根据状态查找用户
     */
    List<User> findByStatus(Integer status);
    
    /**
     * 查询VIP即将过期的用户
     */
    @Query("SELECT u FROM User u WHERE u.vipExpireAt IS NOT NULL AND u.vipExpireAt <= :expireDate")
    List<User> findVipExpiringUsers(@Param("expireDate") Date expireDate);
    
    /**
     * 统计用户数量
     */
    long countByRole(Integer role);
    
    /**
     * 统计活跃用户数量
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.lastLoginAt >= :date")
    long countActiveUsers(@Param("date") Date date);
    
    /**
     * 查询活跃用户列表（最近30天有登录）
     */
    @Query("SELECT u FROM User u WHERE u.lastLoginAt >= :date AND u.status = 1")
    List<User> findActiveUsers(@Param("date") Date date);
}