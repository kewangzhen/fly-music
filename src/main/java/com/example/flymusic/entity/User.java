package com.example.flymusic.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.util.Date;
import java.util.Set;

/**
 * 用户实体类
 * 支持用户登录注册、个人信息管理、VIP会员等功能
 */
@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "username", unique = true, nullable = false, length = 50)
    private String username;
    
    @Column(name = "password", nullable = false, length = 255)
    private String password;
    
    @Column(name = "email", unique = true, length = 100)
    private String email;
    
    @Column(name = "phone", unique = true, length = 20)
    private String phone;
    
    @Column(name = "avatar", length = 255)
    private String avatar;
    
    @Column(name = "nickname", length = 50)
    private String nickname;
    
    @Column(name = "gender")
    private Integer gender;
    
    @Column(name = "birthdate")
    private Date birthdate;
    
    @Column(name = "role", columnDefinition = "TINYINT DEFAULT 0")
    private Integer role;
    
    @Column(name = "status", columnDefinition = "TINYINT DEFAULT 1")
    private Integer status;
    
    @Column(name = "vip_expire_at")
    private Date vipExpireAt;
    
    @Column(name = "created_at", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Date createdAt;
    
    @Column(name = "updated_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private Date updatedAt;
    
    @Column(name = "last_login_at")
    private Date lastLoginAt;
    
    @Column(name = "reset_token", length = 255)
    private String resetToken;
    
    @Column(name = "reset_token_expire_at")
    private Date resetTokenExpireAt;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<Playlist> playlists;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<PlayHistory> playHistories;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<UserFavorite> favorites;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<Post> posts;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<AiMusicGeneration> aiGenerations;
}