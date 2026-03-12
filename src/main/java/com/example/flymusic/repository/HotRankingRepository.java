package com.example.flymusic.repository;

import com.example.flymusic.entity.HotRanking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface HotRankingRepository extends JpaRepository<HotRanking, Long> {
    
    List<HotRanking> findByRankingTypeAndCategoryIdIsNullOrderByRankAsc(String rankingType);
    
    List<HotRanking> findByRankingTypeAndCategoryIdOrderByRankAsc(String rankingType, Long categoryId);
    
    @Modifying
    @Query("DELETE FROM HotRanking WHERE rankingType = :rankingType")
    void deleteByRankingType(@Param("rankingType") String rankingType);
    
    @Modifying
    @Query("DELETE FROM HotRanking WHERE createdAt < :date")
    void deleteOldRankings(@Param("date") Date date);
}
