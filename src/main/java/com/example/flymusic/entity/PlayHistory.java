package com.example.flymusic.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.util.Date;

/**
 * 播放历史实体类
 * 记录用户播放歌曲的详情，用于推荐算法和听歌报告
 */
@Data
@Entity
@Table(name = "play_history")
public class PlayHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "song_id", nullable = false)
    private Song song;
    
    @Column(name = "played_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Date playedAt;
    
    @Column(name = "play_duration")
    private Integer playDuration;
    
    @Column(name = "progress")
    private Integer progress;
}