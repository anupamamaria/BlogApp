package com.blog.blogapp.service;
import com.blog.blogapp.entity.Comment;
import com.blog.blogapp.entity.Post;
import com.blog.blogapp.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;

    public List<Comment> findCommentsByPost(Post post) {
        return commentRepository.findByPost(post);
    }

    public Optional<Comment> findById(Long id) {
        return commentRepository.findById(id);
    }


    public Comment createComment(Comment comment) {
        comment.setCreatedAt(java.time.LocalDateTime.now());
        comment.setUpdatedAt(java.time.LocalDateTime.now());
        return commentRepository.save(comment);
    }

    public Comment updateComment(Long id, Comment updatedComment) {
        Comment comment = commentRepository.findById(id).orElseThrow();
        comment.setComment(updatedComment.getComment());
        comment.setUpdatedAt(java.time.LocalDateTime.now());
        return commentRepository.save(comment);
    }

    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }

}
