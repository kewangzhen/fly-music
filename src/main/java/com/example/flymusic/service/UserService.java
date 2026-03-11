package com.example.flymusic.service;

import com.example.flymusic.entity.User;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 用户Service接口
 * 定义用户相关的业务方法
 */
public interface UserService {
    
    /**
     * 用户注册
     */
    User register(User user);
    
    /**
     * 用户登录
     */
    User login(String username, String password);
    
    /**
     * 根据ID获取用户
     */
    Optional<User> getUserById(Long id);
    
    /**
     * 根据用户名获取用户
     */
    Optional<User> getUserByUsername(String username);
    
    /**
     * 更新用户信息
     */
    User updateUser(Long id, User user);
    
    /**
     * 修改密码
     */
    boolean changePassword(Long userId, String oldPassword, String newPassword);
    
    /**
     * 重置密码
     */
    boolean resetPassword(String email);
    
    /**
     * 更新密码（通过重置令牌）
     */
    boolean updatePasswordByToken(String token, String newPassword);
    
    /**
     * 验证重置令牌
     */
    boolean validateResetToken(String token);
    
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
     * 更新最后登录时间
     */
    void updateLastLoginTime(Long userId);
    
    /**
     * 启用/禁用用户
     */
    void updateUserStatus(Long userId, Integer status);
    
    /**
     * 设置VIP
     */
    void setVip(Long userId, Date expireDate);
    
    /**
     * 获取所有用户
     */
    List<User> getAllUsers();
    
    /**
     * 根据角色获取用户
     */
    List<User> getUsersByRole(Integer role);
    
    /**
     * 获取VIP即将过期的用户
     */
    List<User> getVipExpiringUsers(Date date);
    
    /**
     * 统计用户数量
     */
    long countUsers();
    
    /**
     * 统计活跃用户数量
     */
    long countActiveUsers(Date date);
}