package com.example.flymusic.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.util.Date;

/**
 * 系统日志实体类
 * 用于记录用户操作和管理员操作
 */
@Data
@Entity
@Table(name = "system_logs")
public class SystemLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id")
    private Long userId;
    
    @Column(name = "username", length = 50)
    private String username;
    
    @Column(name = "action", nullable = false, length = 100)
    private String action;
    
    @Column(name = "target_type", length = 50)
    private String targetType;
    
    @Column(name = "target_id")
    private Long targetId;
    
    @Column(name = "method", length = 10)
    private String method;
    
    @Column(name = "ip_address", length = 50)
    private String ipAddress;
    
    @Column(name = "user_agent", length = 500)
    private String userAgent;
    
    @Column(name = "details", columnDefinition = "TEXT")
    private String details;
    
    @Column(name = "status", columnDefinition = "TINYINT DEFAULT 1")
    private Integer status;
    
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
    
    @Column(name = "created_at", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Date createdAt;
    
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = new Date();
        }
    }
}