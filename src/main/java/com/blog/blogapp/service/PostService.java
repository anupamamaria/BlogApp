package com.blog.blogapp.service;
import com.blog.blogapp.entity.Post;
import com.blog.blogapp.entity.Tag;
import com.blog.blogapp.repository.PostRepository;
import com.blog.blogapp.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class PostService {
    private final PostRepository postRepository;
    private final TagRepository tagRepository;

    @Autowired
    public PostService(PostRepository postRepository, TagRepository tagRepository) {
        this.postRepository = postRepository;
        this.tagRepository = tagRepository;
    }


//    public Page<Post> getAllPosts(Pageable pageable) {
//        return postRepository.findAll(pageable);
//    }

    // NEW — supports filtering, sorting, and pagination
//    public Page<Post> filterPosts(String author, LocalDateTime publishedAt, Pageable pageable) {
//        return postRepository.findByAuthorContainingAndPublishedAtBetween(
//                author, publishedAt, pageable);
//    }


//    public List<Post> findAllPosts() {
//        return postRepository.findAll();
//    }

    public Page<Post> getFilteredPosts(String author, String tag, LocalDateTime startOfDay, LocalDateTime endOfDay, Pageable pageable) {
        // If tag is empty, use a query without tag filtering
        if (tag == null || tag.isEmpty()) {
            if (startOfDay != null && endOfDay != null) {
                return postRepository.findByAuthorAndPublishedDate(author, startOfDay, endOfDay, pageable);
            } else {
                return postRepository.findByAuthorContaining(author, pageable);
            }
        } else {
            // If tag is specified, use queries with tag filtering
            if (startOfDay != null && endOfDay != null) {
                return postRepository.findByAuthorTagAndPublishedDate(author, tag, startOfDay, endOfDay, pageable);
            } else {
                return postRepository.findByAuthorAndTag(author, tag, pageable);
            }
        }
    }

    public Optional<Post> findPostById(Long id) {
        return postRepository.findById(id);
    }

    public Post createPost(Post post) {
        post.setCreatedAt(java.time.LocalDateTime.now());
        post.setUpdatedAt(java.time.LocalDateTime.now());
        return postRepository.save(post);
    }



    public Post updatePost(Long id, Post postDetails) {
        Post post = postRepository.findById(id).orElseThrow();
        post.setTitle(postDetails.getTitle());
        post.setContent(postDetails.getContent());
        post.setExcerpt(postDetails.getExcerpt());
        post.setAuthor(postDetails.getAuthor());
        post.setPublishedAt(postDetails.getPublishedAt());
        post.setUpdatedAt(java.time.LocalDateTime.now());
        post.setTags(postDetails.getTags());
        return postRepository.save(post);
    }

    // Helper methods for tag management
    public Post addTagToPost(Long postId, Long tagId) {
        Post post = postRepository.findById(postId).orElseThrow();
        Tag tag = tagRepository.findById(tagId).orElseThrow();
        post.getTags().add(tag);
        return postRepository.save(post);
    }

    public Post removeTagFromPost(Long postId, Long tagId) {
        Post post = postRepository.findById(postId).orElseThrow();
        post.getTags().removeIf(tag -> tag.getId().equals(tagId));
        return postRepository.save(post);
    }


    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }
}
