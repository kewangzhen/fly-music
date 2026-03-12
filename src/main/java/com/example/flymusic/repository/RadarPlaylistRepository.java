package com.example.flymusic.repository;

import com.example.flymusic.entity.RadarPlaylist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public interface RadarPlaylistRepository extends JpaRepository<RadarPlaylist, Long> {
    
    Optional<RadarPlaylist> findByUserIdAndExpiresAtAfter(Long userId, Date now);
    
    @Query("SELECT rp FROM RadarPlaylist rp WHERE rp.expiresAt < :now")
    void deleteExpired(@Param("now") Date now);
    
    long countByExpiresAtAfter(Date now);
}
