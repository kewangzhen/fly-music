package com.example.flymusic.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.util.Date;

/**
 * 用户关注实体类
 * 实现社交功能，用户可以相互关注
 */
@Data
@Entity
@Table(name = "user_follows")
public class UserFollow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "follower_id", nullable = false)
    private User follower;
    
    @ManyToOne
    @JoinColumn(name = "followed_id", nullable = false)
    private User followed;
    
    @Column(name = "created_at", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Date createdAt;
}