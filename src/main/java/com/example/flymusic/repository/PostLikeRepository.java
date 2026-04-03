package com.example.flymusic.repository;

import com.example.flymusic.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    
    Optional<PostLike> findByUserIdAndPostId(Long userId, Long postId);
    
    boolean existsByUserIdAndPostId(Long userId, Long postId);
    
    void deleteByUserIdAndPostId(Long userId, Long postId);
    
    long countByPostId(Long postId);
    
    @Query("SELECT pl.post.id, COUNT(pl) FROM PostLike pl WHERE pl.user.id IN :userIds GROUP BY pl.post.id")
    List<Object[]> countByPostIdIn(@Param("userIds") List<Long> userIds);
}
