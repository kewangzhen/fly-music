package com.example.flymusic.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.util.Date;

/**
 * 分类实体类
 * 用于音乐的风格、流派等分类
 */
@Data
@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", nullable = false, length = 50)
    private String name;
    
    @Column(name = "icon", length = 255)
    private String icon;
    
    @Column(name = "parent_id")
    private Long parentId;
    
    @Column(name = "level", columnDefinition = "TINYINT DEFAULT 1")
    private Integer level;
    
    @Column(name = "sort_order", columnDefinition = "INT DEFAULT 0")
    private Integer sortOrder;
    
    @Column(name = "status", columnDefinition = "TINYINT DEFAULT 1")
    private Integer status;
    
    @Column(name = "created_at", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Date createdAt;
    
    @Column(name = "updated_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private Date updatedAt;
}