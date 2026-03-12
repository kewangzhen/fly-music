package com.example.flymusic.repository;

import com.example.flymusic.entity.UserBehavior;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface UserBehaviorRepository extends JpaRepository<UserBehavior, Long> {
    
    List<UserBehavior> findByUserIdAndCreatedAtAfterOrderByCreatedAtDesc(Long userId, Date date);
    
    @Query("SELECT ub.songId, SUM(ub.weight) FROM UserBehavior ub WHERE ub.userId = :userId AND ub.createdAt > :startDate GROUP BY ub.songId")
    List<Object[]> calculateSongScores(@Param("userId") Long userId, @Param("startDate") Date startDate);
    
    @Query("SELECT COUNT(ub) FROM UserBehavior ub WHERE ub.userId = :userId")
    long countByUserId(@Param("userId") Long userId);
    
    void deleteByUserIdAndCreatedAtBefore(Long userId, Date date);
}
