package com.example.flymusic.entity;

import lombok.Data;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Date;

/**
 * 艺术家实体类
 * 包含歌手信息
 */
@Data
@Entity
@Table(name = "artists")
public class Artist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", nullable = false, length = 50)
    private String name;
    
    @Column(name = "avatar")
    private String avatar;
    
    @Column(name = "cover")
    private String cover;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "gender")
    private Integer gender;
    
    @Column(name = "birthdate")
    private Date birthdate;
    
    @Column(name = "region")
    private String region;
    
    @Column(name = "song_count", columnDefinition = "INT DEFAULT 0")
    private Integer songCount;
    
    @Column(name = "album_count", columnDefinition = "INT DEFAULT 0")
    private Integer albumCount;
    
    @Column(name = "fan_count", columnDefinition = "INT DEFAULT 0")
    private Integer fanCount;
    
    @Column(name = "status", columnDefinition = "TINYINT DEFAULT 1")
    private Integer status;
    
    @Column(name = "created_at", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Date createdAt;
    
    @Column(name = "updated_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private Date updatedAt;
}