package com.example.flymusic.entity;

import lombok.Data;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Date;
import java.util.Set;

/**
 * 播放列表实体类
 * 支持用户创建和管理播放列表，包括系统推荐歌单和用户自定义歌单
 */
@Data
@Entity
@Table(name = "playlists")
public class Playlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "cover", length = 255)
    private String cover;
    
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @JsonIgnore
    @ManyToMany
    @JoinTable(
        name = "playlist_songs",
        joinColumns = @JoinColumn(name = "playlist_id"),
        inverseJoinColumns = @JoinColumn(name = "song_id")
    )
    private Set<Song> songs;
    
    @Column(name = "is_public", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean isPublic;
    
    @Column(name = "song_count", columnDefinition = "INT DEFAULT 0")
    private Integer songCount;
    
    @Column(name = "play_count", columnDefinition = "BIGINT DEFAULT 0")
    private Long playCount;
    
    @Column(name = "type", columnDefinition = "TINYINT DEFAULT 0")
    private Integer type;
    
    @Column(name = "status", columnDefinition = "TINYINT DEFAULT 1")
    private Integer status;
    
    @Column(name = "created_at", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Date createdAt;
    
    @Column(name = "updated_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private Date updatedAt;
}