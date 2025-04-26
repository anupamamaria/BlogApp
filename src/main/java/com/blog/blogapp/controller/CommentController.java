package com.blog.blogapp.controller;
import com.blog.blogapp.entity.Comment;
import com.blog.blogapp.entity.Post;
import com.blog.blogapp.entity.User;
import com.blog.blogapp.service.CommentService;
import com.blog.blogapp.service.PostService;
import com.blog.blogapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private PostService postService;

    @PostMapping("/add")
    public String addComment(@ModelAttribute("commentForm") Comment comment) {
        //@RequestParam int postId
        int postId = comment.getPost().getId();
        Post post = postService.findPostById(postId).orElseThrow();
        comment.setPost(post);
        comment.setCreatedAt(LocalDateTime.now());
        commentService.createComment(comment);
        return "redirect:/posts/" + postId;
    }


    // Display the comment edit form
    @GetMapping("/{id}/update")
    public String editCommentForm(@PathVariable int id, Principal principal, Model model) {

        // Get the logged-in user's email
        String email = principal.getName();
        // Find the logged-in user (make sure you have a UserService to fetch user details)
        User loggedInUser = userService.findByEmail(email);


        // Fetch the comment from the database using its id
        Comment comment = commentService.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        int postId = comment.getPost().getId();

        // Check if the logged-in user is the author of the comment
        if (!comment.getEmail().equals(loggedInUser.getEmail())) {
            return "redirect:/login";  // or an error page
        }

        model.addAttribute("commentForm", comment);
        model.addAttribute("postId", postId);
       model.addAttribute("loggedInUser", loggedInUser);
        return "comments/edit"; // Make sure you have 'edit.html' template
    }


    @PostMapping("/{id}/update")
    public String updateComment(@ModelAttribute("commentForm") Comment comment, @RequestParam int postId) {
        // Update the comment based on the given id
        commentService.updateComment(comment.getId(), comment);

        // Redirect back to the post details page after updating the comment
        return "redirect:/posts/" + postId;
    }


    @PostMapping("/{id}/delete")
    public String deleteComment(@PathVariable int id, @RequestParam int postId) {
        commentService.deleteComment(id);
        return "redirect:/posts/" + postId;
    }

}
