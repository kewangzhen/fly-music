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

    @Query("SELECT h FROM HotRanking h WHERE h.rankingType = :rankingType AND h.categoryId IS NULL ORDER BY h.rank ASC")
    List<HotRanking> findGlobalByRankingType(@Param("rankingType") String rankingType);

    @Query("SELECT h FROM HotRanking h WHERE h.rankingType = :rankingType AND h.categoryId = :categoryId ORDER BY h.rank ASC")
    List<HotRanking> findByRankingTypeAndCategory(@Param("rankingType") String rankingType, @Param("categoryId") Long categoryId);

    @Modifying
    @Query("DELETE FROM HotRanking WHERE rankingType = :rankingType")
    void deleteByRankingType(@Param("rankingType") String rankingType);

    @Modifying
    @Query("DELETE FROM HotRanking WHERE createdAt < :date")
    void deleteOldRankings(@Param("date") Date date);
}
