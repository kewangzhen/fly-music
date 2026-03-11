package com.example.flymusic.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.util.Date;
import java.util.Set;

/**
 * 艺术家实体类
 * 包含歌手信息和作品集
 */
@Data
@Entity
@Table(name = "artists")
public class Artist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @Column(name = "avatar", length = 255)
    private String avatar;
    
    @Column(name = "cover", length = 255)
    private String cover;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "gender")
    private Integer gender;
    
    @Column(name = "birthdate")
    private Date birthdate;
    
    @Column(name = "region", length = 50)
    private String region;
    
    @Column(name = "fan_count", columnDefinition = "INT DEFAULT 0")
    private Integer fanCount;
    
    @Column(name = "song_count", columnDefinition = "INT DEFAULT 0")
    private Integer songCount;
    
    @Column(name = "album_count", columnDefinition = "INT DEFAULT 0")
    private Integer albumCount;
    
    @Column(name = "status", columnDefinition = "TINYINT DEFAULT 1")
    private Integer status;
    
    @Column(name = "created_at", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Date createdAt;
    
    @Column(name = "updated_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private Date updatedAt;
    
    @ManyToMany(mappedBy = "artists")
    private Set<Song> songs;
}