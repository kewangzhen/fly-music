package com.example.flymusic.repository;

import com.example.flymusic.entity.ArtistFollow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArtistFollowRepository extends JpaRepository<ArtistFollow, Long> {
    
    List<ArtistFollow> findByUserId(Long userId);
    
    Optional<ArtistFollow> findByUserIdAndArtistId(Long userId, Long artistId);
    
    boolean existsByUserIdAndArtistId(Long userId, Long artistId);
    
    void deleteByUserIdAndArtistId(Long userId, Long artistId);
    
    long countByArtistId(Long artistId);
}
