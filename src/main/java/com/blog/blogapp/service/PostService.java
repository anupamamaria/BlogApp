package com.blog.blogapp.service;
import com.blog.blogapp.entity.Post;
import com.blog.blogapp.entity.Tag;
import com.blog.blogapp.entity.User;
import com.blog.blogapp.repository.PostRepository;
import com.blog.blogapp.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.*;

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


    public Page<Post> getFilteredPosts(List<String> authors, List<String> tags,
                                       LocalDateTime startOfDay, LocalDateTime endOfDay, String searchTerm,
                                       Pageable pageable) {

        return postRepository.findPostsWithFiltersSearchAndAllTags(authors, tags, startOfDay, endOfDay, searchTerm,pageable);
    }

    public List<String> findAllAuthors() {
        List<Post> posts = postRepository.findAll(); // Fetch all posts
        Set<String> authors = new HashSet<>(); // Use a Set to ensure uniqueness

        for (Post post : posts) {
            User author = post.getAuthor();
            if (author.getName() != null && !authors.contains(author.getName())) {
                authors.add(author.getName()); // Add the author to the Set if not already present
            }
        }

        return new ArrayList<>(authors); // Convert the Set to a List
    }


    public Optional<Post> findPostById(int id) {
        return postRepository.findById(id);
    }

    public Post createPost(Post post) {
        post.setCreatedAt(java.time.LocalDateTime.now());
        post.setPublishedAt(java.time.LocalDateTime.now());
        post.setUpdatedAt(java.time.LocalDateTime.now());
        post.generateExcerpt();
        return postRepository.save(post);
    }



    public Post updatePost(int id, Post postDetails) {
        Post post = postRepository.findById(id).orElseThrow();
        post.setTitle(postDetails.getTitle());
        post.setContent(postDetails.getContent());
        post.generateExcerpt();
        post.setAuthor(postDetails.getAuthor());
        post.setUpdatedAt(java.time.LocalDateTime.now());
        post.setTags(postDetails.getTags());
        return postRepository.save(post);
    }

    // Helper methods for tag management
    public Post addTagToPost(int postId, int tagId) {
        Post post = postRepository.findById(postId).orElseThrow();
        Tag tag = tagRepository.findById(tagId).orElseThrow();
        post.getTags().add(tag);
        return postRepository.save(post);
    }

    public Post removeTagFromPost(int postId, int tagId) {
        Post post = postRepository.findById(postId).orElseThrow();
        post.getTags().removeIf(tag -> tag.getId()==(tagId));
        return postRepository.save(post);
    }


    public void deletePost(int id) {
        postRepository.deleteById(id);
    }
}
