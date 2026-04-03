package com.example.flymusic.repository;

import com.example.flymusic.entity.PlaylistFavorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlaylistFavoriteRepository extends JpaRepository<PlaylistFavorite, Long> {
    
    List<PlaylistFavorite> findByUserId(Long userId);
    
    Optional<PlaylistFavorite> findByUserIdAndPlaylistId(Long userId, Long playlistId);
    
    boolean existsByUserIdAndPlaylistId(Long userId, Long playlistId);
    
    void deleteByUserIdAndPlaylistId(Long userId, Long playlistId);
    
    long countByPlaylistId(Long playlistId);
}
