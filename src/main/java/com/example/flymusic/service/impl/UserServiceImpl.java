package com.example.flymusic.service.impl;

import com.example.flymusic.entity.User;
import com.example.flymusic.repository.UserRepository;
import com.example.flymusic.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 用户Service实现类
 * 实现用户相关的业务逻辑
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 用户注册
     */
    @Override
    @Transactional
    public User register(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        if (user.getEmail() != null && userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("邮箱已被注册");
        }
        if (user.getPhone() != null && userRepository.existsByPhone(user.getPhone())) {
            throw new RuntimeException("手机号已被注册");
        }
        
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(0);
        user.setStatus(1);
        user.setCreatedAt(new Date());
        
        return userRepository.save(user);
    }

    /**
     * 用户登录
     */
    @Override
    public User login(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (!userOpt.isPresent()) {
            throw new RuntimeException("用户不存在");
        }
        
        User user = userOpt.get();
        if (user.getStatus() == 0) {
            throw new RuntimeException("账号已被禁用");
        }
        
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        
        updateLastLoginTime(user.getId());
        return user;
    }

    /**
     * 根据ID获取用户
     */
    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * 根据用户名获取用户
     */
    @Override
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * 更新用户信息
     */
    @Override
    @Transactional
    public User updateUser(Long id, User user) {
        Optional<User> existingUserOpt = userRepository.findById(id);
        if (!existingUserOpt.isPresent()) {
            throw new RuntimeException("用户不存在");
        }
        
        User existingUser = existingUserOpt.get();
        
        if (user.getNickname() != null) {
            existingUser.setNickname(user.getNickname());
        }
        if (user.getAvatar() != null) {
            existingUser.setAvatar(user.getAvatar());
        }
        if (user.getGender() != null) {
            existingUser.setGender(user.getGender());
        }
        if (user.getBirthdate() != null) {
            existingUser.setBirthdate(user.getBirthdate());
        }
        if (user.getEmail() != null && !user.getEmail().equals(existingUser.getEmail())) {
            if (userRepository.existsByEmail(user.getEmail())) {
                throw new RuntimeException("邮箱已被使用");
            }
            existingUser.setEmail(user.getEmail());
        }
        if (user.getPhone() != null && !user.getPhone().equals(existingUser.getPhone())) {
            if (userRepository.existsByPhone(user.getPhone())) {
                throw new RuntimeException("手机号已被使用");
            }
            existingUser.setPhone(user.getPhone());
        }
        
        existingUser.setUpdatedAt(new Date());
        return userRepository.save(existingUser);
    }

    /**
     * 修改密码
     */
    @Override
    @Transactional
    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            return false;
        }
        
        User user = userOpt.get();
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return false;
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(new Date());
        userRepository.save(user);
        return true;
    }

    /**
     * 重置密码
     */
    @Override
    @Transactional
    public boolean resetPassword(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (!userOpt.isPresent()) {
            return false;
        }
        
        User user = userOpt.get();
        String token = UUID.randomUUID().toString();
        Date expireDate = new Date(System.currentTimeMillis() + 3600000);
        
        user.setResetToken(token);
        user.setResetTokenExpireAt(expireDate);
        user.setUpdatedAt(new Date());
        userRepository.save(user);
        
        return true;
    }

    /**
     * 更新密码（通过重置令牌）
     */
    @Override
    @Transactional
    public boolean updatePasswordByToken(String token, String newPassword) {
        Optional<User> userOpt = userRepository.findByResetToken(token);
        if (!userOpt.isPresent()) {
            return false;
        }
        
        User user = userOpt.get();
        if (user.getResetTokenExpireAt().before(new Date())) {
            return false;
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpireAt(null);
        user.setUpdatedAt(new Date());
        userRepository.save(user);
        return true;
    }

    /**
     * 验证重置令牌
     */
    @Override
    public boolean validateResetToken(String token) {
        Optional<User> userOpt = userRepository.findByResetToken(token);
        if (!userOpt.isPresent()) {
            return false;
        }
        
        User user = userOpt.get();
        return user.getResetTokenExpireAt() != null && 
               user.getResetTokenExpireAt().after(new Date());
    }

    /**
     * 检查用户名是否存在
     */
    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * 检查邮箱是否存在
     */
    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * 检查手机号是否存在
     */
    @Override
    public boolean existsByPhone(String phone) {
        return userRepository.existsByPhone(phone);
    }

    /**
     * 更新最后登录时间
     */
    @Override
    @Transactional
    public void updateLastLoginTime(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setLastLoginAt(new Date());
            userRepository.save(user);
        }
    }

    /**
     * 启用/禁用用户
     */
    @Override
    @Transactional
    public void updateUserStatus(Long userId, Integer status) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setStatus(status);
            user.setUpdatedAt(new Date());
            userRepository.save(user);
        }
    }

    /**
     * 设置VIP
     */
    @Override
    @Transactional
    public void setVip(Long userId, Date expireDate) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setRole(1);
            user.setVipExpireAt(expireDate);
            user.setUpdatedAt(new Date());
            userRepository.save(user);
        }
    }

    /**
     * 获取所有用户
     */
    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * 根据角色获取用户
     */
    @Override
    public List<User> getUsersByRole(Integer role) {
        return userRepository.findByRole(role);
    }

    /**
     * 获取VIP即将过期的用户
     */
    @Override
    public List<User> getVipExpiringUsers(Date date) {
        return userRepository.findVipExpiringUsers(date);
    }

    /**
     * 统计用户数量
     */
    @Override
    public long countUsers() {
        return userRepository.count();
    }

    /**
     * 统计活跃用户数量
     */
    @Override
    public long countActiveUsers(Date date) {
        return userRepository.countActiveUsers(date);
    }
}