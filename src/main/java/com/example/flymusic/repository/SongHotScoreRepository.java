package com.example.flymusic.repository;

import com.example.flymusic.entity.SongHotScore;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SongHotScoreRepository extends JpaRepository<SongHotScore, Long> {
    
    Optional<SongHotScore> findBySongId(Long songId);
    
    @Query("SELECT shs FROM SongHotScore shs ORDER BY shs.hotScore DESC")
    List<SongHotScore> findAllByOrderByHotScoreDesc(Pageable pageable);
    
    @Query("SELECT shs FROM SongHotScore shs JOIN Song s ON shs.songId = s.id " +
           "WHERE s.category.id = :categoryId ORDER BY shs.hotScore DESC")
    List<SongHotScore> findByCategoryIdOrderByHotScoreDesc(@Param("categoryId") Long categoryId, Pageable pageable);
    
    @Query("SELECT shs FROM SongHotScore shs JOIN Song s ON shs.songId = s.id " +
           "WHERE s.createdAt >= :startDate ORDER BY shs.hotScore DESC")
    List<SongHotScore> findNewSongsOrderByHotScoreDesc(@Param("startDate") java.util.Date startDate, Pageable pageable);
}
