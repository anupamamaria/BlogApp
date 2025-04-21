package com.blog.blogapp.controller;
import com.blog.blogapp.entity.Post;
import com.blog.blogapp.entity.Comment;
import com.blog.blogapp.service.PostService;
import com.blog.blogapp.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/posts")
public class PostController {
    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    // List all posts
    @GetMapping
    public String listPosts(@RequestParam(defaultValue = "publishedAt") String sortBy,
                             @RequestParam(defaultValue = "desc") String sortOrder,
                            @RequestParam(defaultValue = "") String author,
//                            @RequestParam(defaultValue = "") String tag,
                            @RequestParam(defaultValue = "") String publishedDate,
                            @RequestParam(defaultValue = "0") int page,
                            Model model) {
//        List<Post> posts = postService.findAllPosts();
//        model.addAttribute("posts", posts);
//        return "posts/list"; // Thymeleaf template

        int pageSize = 10; // max 10 posts per page
        Sort sort = sortOrder.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, pageSize, sort);

        // Convert startOfDay and endOfDay to LocalDateTime (startOfDay and endOfDay)
        LocalDateTime startOfDayTime = null;
        LocalDateTime endOfDayTime = null;


        if (!publishedDate.isEmpty()) {
            try {
                LocalDate date = LocalDate.parse(publishedDate);
                startOfDayTime = date.atStartOfDay();
                endOfDayTime = date.atTime(LocalTime.MAX);
            } catch (Exception e) {
                System.out.println("Invalid date format: " + e.getMessage());
            }
        }

        System.out.println("Filtering by: author=" + author + ", startOfDay=" + startOfDayTime + ", endOfDay=" + endOfDayTime);

        Page<Post> postPage = postService.getFilteredPosts(author, startOfDayTime, endOfDayTime, pageable);


        model.addAttribute("postPage", postPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", postPage.getTotalPages());


        // Preserve filters/sorting in the view
        model.addAttribute("author", author);
        //model.addAttribute("tag", tag);
        model.addAttribute("publishedDate", publishedDate);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortOrder", sortOrder);

        return "posts/list";
    }

    // View single post with comments
//    @GetMapping("/{id}")
//    public String viewPost(@PathVariable Long id, Model model) {
//        Post post = postService.findPostById(id).orElseThrow();
//        List<Comment> comments = commentService.findCommentsByPost(post);
//
//        Comment newComment = new Comment();
//        newComment.setPost(post);
//
//        model.addAttribute("post", post);
//        model.addAttribute("comments", comments);
//        model.addAttribute("comment", newComment);
//
//        return "posts/view";
//    }
    @GetMapping("/{id}")
    public String viewPost(@PathVariable Long id, Model model) {
        Post post = postService.findPostById(id).orElseThrow();
        List<Comment> comments = commentService.findCommentsByPost(post);
        model.addAttribute("post", post);
        model.addAttribute("comments", comments);
        model.addAttribute("commentForm", new Comment()); // Note the name "commentForm"
        return "posts/view";
    }

    // Show form to create post
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("post", new Post());
        return "posts/create";
    }

    // Submit new post
    @PostMapping
    public String createPost(@ModelAttribute Post post) {
        postService.createPost(post);
        return "redirect:/posts";
    }

    // Show form to edit post
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Post post = postService.findPostById(id).orElseThrow();
        model.addAttribute("post", post);
        return "posts/edit";
    }

    // Submit update
    @PostMapping("/{id}/update")
    public String updatePost(@PathVariable Long id, @ModelAttribute Post post) {
        postService.updatePost(id, post);
        return "redirect:/posts";
    }

    // Delete post
    @PostMapping("/{id}/delete")
    public String deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return "redirect:/posts";
    }
}
