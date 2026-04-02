package com.example.flymusic.entity;

import lombok.Data;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Date;

/**
 * 专辑实体类
 * 包含专辑信息和歌曲列表
 */
@Data
@Entity
@Table(name = "albums")
public class Album {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "title", nullable = false, length = 100)
    private String title;
    
    @Column(name = "cover")
    private String cover;
    
    @Column(name = "release_date")
    private Date releaseDate;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @JsonIgnore
    @JsonIgnoreProperties({"songs", "albums"})
    @ManyToOne
    @JoinColumn(name = "artist_id")
    private Artist artist;
    
    @Column(name = "song_count", columnDefinition = "INT DEFAULT 0")
    private Integer songCount;
    
    @Column(name = "play_count", columnDefinition = "BIGINT DEFAULT 0")
    private Long playCount;
    
    @Column(name = "status", columnDefinition = "TINYINT DEFAULT 1")
    private Integer status;
    
    @Column(name = "created_at", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Date createdAt;
    
    @Column(name = "updated_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private Date updatedAt;
}