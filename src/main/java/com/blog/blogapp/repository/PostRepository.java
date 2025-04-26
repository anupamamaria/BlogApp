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
public interface PostRepository  extends JpaRepository<Post, Integer>, JpaSpecificationExecutor<Post> {
    Page<Post> findAll(Pageable pageable);

    @Query("SELECT DISTINCT p FROM Post p " +
            "LEFT JOIN p.tags t " +
            "WHERE (:#{#authors == null or #authors.isEmpty()} = true OR p.author.name IN :authors) " +
            "AND (:#{#tags == null or #tags.isEmpty()} = true OR t.name IN :tags) " +
            "AND (CAST(:startDate AS timestamp) IS NULL OR p.publishedAt >= :startDate) " +
            "AND (CAST(:endDate AS timestamp) IS NULL OR p.publishedAt <= :endDate) " +
            "AND (:searchTerm IS NULL OR :searchTerm = '' OR "+
            "CONCAT(' ', p.title, ' ') ILIKE CONCAT('% ', CAST(:searchTerm AS string), ' %') OR " +
            "CONCAT(' ', p.content, ' ') ILIKE CONCAT('% ', CAST(:searchTerm AS string), ' %') OR " +
            "CONCAT(' ', p.author.name, ' ') ILIKE CONCAT('% ', CAST(:searchTerm AS string), ' %') OR " +
            "CONCAT(' ', t.name, ' ') ILIKE CONCAT('% ', CAST(:searchTerm AS string), ' %')) " +
            "GROUP BY p " +
            "HAVING (:#{#tags == null or #tags.isEmpty()} = true OR COUNT(DISTINCT t.name) = :#{#tags.size()})")
    Page<Post> findPostsWithFiltersSearchAndAllTags(
            @Param("authors") List<String> authors,
            @Param("tags") List<String> tags,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("searchTerm") String searchTerm,
            Pageable pageable
    );


}
