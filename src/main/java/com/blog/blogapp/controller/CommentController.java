package com.blog.blogapp.controller;
import com.blog.blogapp.entity.Comment;
import com.blog.blogapp.entity.Post;
import com.blog.blogapp.service.CommentService;
import com.blog.blogapp.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/comments")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @Autowired
    private PostService postService;

     //Add a comment to a post
//    @PostMapping("/add")
//    public String addComment(@ModelAttribute Comment comment, @RequestParam Long postId) {
//        Post post = postService.findPostById(postId).orElseThrow();
//        comment.setPost(post);
//        commentService.createComment(comment);
//        return "redirect:/posts/" + postId;
//    }

    @PostMapping("/add")
    public String addComment(@ModelAttribute("commentForm") Comment comment, @RequestParam Long postId) {
        Post post = postService.findPostById(postId).orElseThrow();
        comment.setPost(post);
        comment.setCreatedAt(LocalDateTime.now());
        commentService.createComment(comment);
        return "redirect:/posts/" + postId;
    }

//    @PostMapping("/add")
//    public String addComment(
//            @RequestParam("name") String name,
//            @RequestParam("email") String email,
//            @RequestParam("comment") String commentText,
//            @RequestParam("postId") Long postId) {
//
//        Post post = postService.findPostById(postId).orElseThrow();
//
//        Comment comment = new Comment();
//        comment.setName(name);
//        comment.setEmail(email);
//        comment.setComment(commentText);
//        comment.setPost(post);
//        comment.setCreatedAt(LocalDateTime.now());
//
//        commentService.createComment(comment);
//
//        return "redirect:/posts/" + postId;
//    }

     //Show form to edit comment
//    @GetMapping("/{id}/edit")
//    public String editCommentForm(@PathVariable Long id, Model model) {
//        Comment comment = commentService.updateComment(id, new Comment()); // dummy to get comment
//        model.addAttribute("comment", comment);
//        return "comments/edit";
//    }

    // Display the comment edit form
    @GetMapping("/{id}/update")
    public String editCommentForm(@PathVariable Long id, Model model) {
        // Fetch the comment from the database using its id
        Comment comment = commentService.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        Long postId = comment.getPost().getId();

        model.addAttribute("commentForm", comment);
        model.addAttribute("postId", postId);
        return "comments/edit"; // Make sure you have 'edit.html' template
    }

//
//    // Submit comment update
//    @PostMapping("/{id}/update")
//    public String updateComment(@PathVariable Long id, @ModelAttribute Comment updatedComment) {
//        Comment comment = commentService.updateComment(id, updatedComment);
//        return "redirect:/posts/" + comment.getPost().getId();
//    }

    @PostMapping("/{id}/update")
    public String updateComment(@ModelAttribute("commentForm") Comment comment, @RequestParam Long postId) {
        // Update the comment based on the given id
        commentService.updateComment(comment.getId(), comment);

        // Redirect back to the post details page after updating the comment
        return "redirect:/posts/" + postId;
    }


    // Delete comment
//    @PostMapping("/{id}/delete")
//    public String deleteComment(@PathVariable Long id) {
//        Comment comment = commentService.findCommentsByPost(null) // need post from comment
//                .stream().filter(c -> c.getId().equals(id)).findFirst().orElseThrow();
//        commentService.deleteComment(id);
//        return "redirect:/posts/" + comment.getPost().getId();
//    }
    @PostMapping("/{id}/delete")
    public String deleteComment(@PathVariable Long id, @RequestParam Long postId) {
        commentService.deleteComment(id);
        return "redirect:/posts/" + postId;
    }

}
