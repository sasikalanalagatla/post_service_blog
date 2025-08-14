package com.mb.postService.repository;

import com.mb.postService.model.Post;
import com.mb.postService.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    
    List<Post> findByAuthorOrderByCreatedAtDesc(String author);
    
    List<Post> findByIsPublishedTrueOrderByCreatedAtDesc();
    
    @Query("SELECT p FROM Post p JOIN p.tags t WHERE t.name = :tag ORDER BY p.createdAt DESC")
    List<Post> findByTagOrderByCreatedAtDesc(@Param("tag") String tag);
    
    @Query("SELECT p FROM Post p WHERE p.title LIKE %:keyword% OR p.content LIKE %:keyword% OR p.author LIKE %:keyword% ORDER BY p.createdAt DESC")
    List<Post> searchByKeyword(@Param("keyword") String keyword);
    
    @Query("SELECT DISTINCT t.name FROM Post p JOIN p.tags t")
    List<String> findAllTags();
}
