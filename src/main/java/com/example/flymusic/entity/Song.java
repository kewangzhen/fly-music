package com.example.flymusic.entity;

import lombok.Data;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Date;
import java.util.Set;

/**
 * 歌曲实体类
 * 支持音乐播放、收藏、推荐等功能
 */
@Data
@Entity
@Table(name = "songs")
public class Song {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "title", nullable = false, length = 100)
    private String title;
    
    @Column(name = "duration")
    private Integer duration;
    
    @Column(name = "url", nullable = false, length = 255)
    private String url;
    
    @Column(name = "lyrics", columnDefinition = "TEXT")
    private String lyrics;
    
    @Column(name = "cover", length = 255)
    private String cover;
    
    @ManyToOne
    @JoinColumn(name = "album_id")
    private Album album;
    
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    
    @Column(name = "category_id", insertable = false, updatable = false)
    private Long categoryId;
    
    @ManyToMany
    @JoinTable(
        name = "song_artists",
        joinColumns = @JoinColumn(name = "song_id"),
        inverseJoinColumns = @JoinColumn(name = "artist_id")
    )
    private Set<Artist> artists;
    
    @Column(name = "play_count", columnDefinition = "BIGINT DEFAULT 0")
    private Long playCount;
    
    @Column(name = "download_count", columnDefinition = "INT DEFAULT 0")
    private Integer downloadCount;
    
    @Column(name = "share_count", columnDefinition = "INT DEFAULT 0")
    private Integer shareCount;
    
    @Column(name = "status", columnDefinition = "TINYINT DEFAULT 1")
    private Integer status;
    
    @Column(name = "is_original", columnDefinition = "TINYINT DEFAULT 0")
    private Integer isOriginal;
    
    @Column(name = "user_id")
    private Long userId;
    
    @Column(name = "user_upload", columnDefinition = "TINYINT DEFAULT 0")
    private Integer userUpload;
    
    @Column(name = "created_at", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Date createdAt;
    
    @Column(name = "updated_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private Date updatedAt;
    
    @JsonIgnore
    @OneToMany(mappedBy = "song", cascade = CascadeType.ALL)
    private Set<PlayHistory> playHistories;
}