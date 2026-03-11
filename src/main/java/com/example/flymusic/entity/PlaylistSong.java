package com.example.flymusic.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "playlist_songs")
public class PlaylistSong {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "playlist_id")
    private Playlist playlist;
    
    @ManyToOne
    @JoinColumn(name = "song_id")
    private Song song;
    
    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;
    
    @Column(name = "added_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Date addedAt;
}
