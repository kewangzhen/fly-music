package com.example.flymusic.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.util.Date;

/**
 * 歌手关注实体类
 */
@Data
@Entity
@Table(name = "artist_follows")
public class ArtistFollow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "artist_id", nullable = false)
    private Long artistId;
    
    @Column(name = "artist_name", length = 100)
    private String artistName;
    
    @Column(name = "artist_avatar", length = 255)
    private String artistAvatar;
    
    @Column(name = "created_at", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Date createdAt;
}
