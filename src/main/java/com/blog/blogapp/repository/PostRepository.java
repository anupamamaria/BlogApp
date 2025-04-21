package com.blog.blogapp.repository;

import com.blog.blogapp.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostRepository  extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {
    Page<Post> findAll(Pageable pageable);
//    Page<Post> findByAuthorContainingAndPublishedAtBetween(
//            String author, LocalDateTime publishedAt, Pageable pageable);



    @Query("SELECT DISTINCT p FROM Post p " +
            "LEFT JOIN p.tags t " +
            "WHERE (:#{#authors == null or #authors.isEmpty()} = true OR p.author IN :authors) " +
            "AND (:#{#tags == null or #tags.isEmpty()} = true OR t.name IN :tags) " +
            "AND (CAST(:startDate AS timestamp) IS NULL OR p.publishedAt >= :startDate) " +
            "AND (CAST(:endDate AS timestamp) IS NULL OR p.publishedAt <= :endDate) " +
            "GROUP BY p " +
            "HAVING (:#{#tags == null or #tags.isEmpty()} = true OR COUNT(DISTINCT t.name) = :#{#tags.size()})")
    Page<Post> findPostsWithFiltersAndAllTags(
            @Param("authors") List<String> authors,
            @Param("tags") List<String> tags,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );

}
