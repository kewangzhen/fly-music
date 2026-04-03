package com.example.flymusic.repository;

import com.example.flymusic.entity.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostCommentRepository extends JpaRepository<PostComment, Long> {
    
    List<PostComment> findByPostIdOrderByCreatedAtDesc(Long postId);
    
    List<PostComment> findByParentIdOrderByCreatedAtAsc(Long parentId);
    
    long countByPostId(Long postId);
    
    @Query("SELECT pc FROM PostComment pc WHERE pc.post.id = :postId AND pc.parentId IS NULL ORDER BY pc.createdAt DESC")
    List<PostComment> findTopLevelComments(@Param("postId") Long postId);
}
