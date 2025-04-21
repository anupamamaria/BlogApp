package com.blog.blogapp.service;
import com.blog.blogapp.entity.Post;
import com.blog.blogapp.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PostService {
    @Autowired
    private PostRepository postRepository;

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

    public Page<Post> getFilteredPosts(String author, LocalDateTime startOfDayTime, LocalDateTime endOfDayTime, Pageable pageable) {
        if (startOfDayTime == null || endOfDayTime == null) {
            return postRepository.findByAuthorContaining(author, pageable);
        } else {
            return postRepository.findByAuthorAndPublishedDate(author, startOfDayTime, endOfDayTime, pageable);
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
        return postRepository.save(post);
    }

    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }
}
