package com.blog.blogapp.repository;

import com.blog.blogapp.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.time.LocalDateTime;

public interface PostRepository  extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {
    Page<Post> findAll(Pageable pageable);
//    Page<Post> findByAuthorContainingAndPublishedAtBetween(
//            String author, LocalDateTime publishedAt, Pageable pageable);

//
//    @Query("SELECT p FROM Post p WHERE (:author = '' OR p.author LIKE %:author%) AND p.publishedAt BETWEEN :startOfDay AND :endOfDay")
//    Page<Post> findByAuthorAndPublishedDateBetween(
//            @Param("author") String author,
//            @Param("startOfDay") LocalDateTime startOfDay,
//            @Param("endOfDay") LocalDateTime endOfDay,
//            Pageable pageable);

    // Find by author only (when no date filter is applied)
    @Query("SELECT p FROM Post p WHERE (:author = '' OR p.author LIKE %:author%)")
    Page<Post> findByAuthorContaining(@Param("author") String author, Pageable pageable);

    // Find by both author and published date
    @Query("SELECT p FROM Post p WHERE (:author = '' OR p.author LIKE %:author%) AND " +
            "p.publishedAt BETWEEN :startOfDay AND :endOfDay")
    Page<Post> findByAuthorAndPublishedDate(
            @Param("author") String author,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay,
            Pageable pageable);

}
