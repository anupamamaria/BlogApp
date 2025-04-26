package com.blog.blogapp.controller;
import com.blog.blogapp.entity.Post;
import com.blog.blogapp.entity.Comment;
import com.blog.blogapp.entity.Tag;
import com.blog.blogapp.entity.User;
import com.blog.blogapp.repository.TagRepository;
import com.blog.blogapp.service.PostService;
import com.blog.blogapp.service.CommentService;
import com.blog.blogapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/posts")
public class PostController {
    @Autowired
    private PostService postService;

    private final UserService userService;

    @Autowired
    public PostController(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private CommentService commentService;

    // List all posts
    @GetMapping
    public String listPosts(@RequestParam(defaultValue = "publishedAt") String sortBy,
                            @RequestParam(defaultValue = "desc") String sortOrder,
                            @RequestParam(defaultValue = "") List<String> authors,
                            @RequestParam(defaultValue = "") List<String> tags,
                            @RequestParam(required = false) String startDate,
                            @RequestParam(required = false) String endDate,
                            @RequestParam(required = false) String search,
                            @RequestParam(defaultValue = "0") int page,
                            Model model , Principal principal){


        User loggedInUser = null;
        if (principal != null) {
            String email = principal.getName();  // Get the logged-in user's email
            loggedInUser = userService.findByEmail(email); // Find the logged-in user
        }

        int pageSize = 10; // max 10 posts per page
        Sort sort = sortOrder.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, pageSize, sort);

        // Convert startOfDay and endOfDay to LocalDateTime (startOfDay and endOfDay)
        LocalDateTime startOfDayTime = null;
        LocalDateTime endOfDayTime = null;



        if (startDate != null && !startDate.isEmpty()) {
            try {
                startOfDayTime = LocalDate.parse(startDate).atStartOfDay();
            } catch (Exception e) {
                System.out.println("Invalid start date format: " + e.getMessage());
            }
        }

        if (endDate != null && !endDate.isEmpty()) {
            try {
                endOfDayTime = LocalDate.parse(endDate).atTime(LocalTime.MAX);
            } catch (Exception e) {
                System.out.println("Invalid end date format: " + e.getMessage());
            }
        }
        Page<Post> postPage = postService.getFilteredPosts(authors, tags, startOfDayTime, endOfDayTime, search,pageable);

        model.addAttribute("postPage", postPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", postPage.getTotalPages());

        // Preserve filters/sorting in the view
        model.addAttribute("authors", authors);
        model.addAttribute("tags", tags);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortOrder", sortOrder);
        model.addAttribute("allTags", tagRepository.findAll());
        model.addAttribute("allAuthors", postService.findAllAuthors());
        model.addAttribute("search",search);

        model.addAttribute("loggedInUser", loggedInUser);
        return "posts/list";
    }

    @GetMapping("/{id}")
    public String viewPost(@PathVariable int id, Model model, Principal principal) {

        User loggedInUser = null;
        if (principal != null) {
            String email = principal.getName();  // Get the logged-in user's email
            loggedInUser = userService.findByEmail(email); // Find the logged-in user
        }

        Post post = postService.findPostById(id).orElseThrow();
        List<Comment> comments = commentService.findCommentsByPost(post);
        model.addAttribute("post", post);
        model.addAttribute("comments", comments);
        model.addAttribute("commentForm", new Comment()); // Note the name "commentForm"
        model.addAttribute("loggedInUser", loggedInUser);
        return "posts/view";
    }

    // Show form to create post
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("post", new Post());
        return "posts/create";
    }

    // Method to handle form submission
    @PostMapping
    public String savePost(@ModelAttribute Post post, @RequestParam("tagString") String tagString,Principal principal) {
        // Split the input string and create tag list

        String email = principal.getName();  // Get logged-in user's email
        User loggedInUser = userService.findByEmail(email);  // Use email to fetch logged-in user

        post.setAuthor(loggedInUser);
        List<String> tagNames = new ArrayList<>();
        if (tagString != null && !tagString.isEmpty()) {
            String[] parts = tagString.split(",");
            for (String part : parts) {
                String trimmedTag = part.trim();
                if (!trimmedTag.isEmpty()) {
                    tagNames.add(trimmedTag);
                }
            }
        }

// Create Tag entities from the names
        Set<Tag> tags = new HashSet<>();
        for (String tagName : tagNames) {
            // Try to find existing tag
            Tag existingTag = tagRepository.findByName(tagName);

            Tag tag;
            if (existingTag != null) {
                // Use existing tag
                tag = existingTag;
            } else {
                // Create new tag
                Tag newTag = new Tag();
                newTag.setName(tagName);
                tag = tagRepository.save(newTag);
            }
            tags.add(tag);
        }

        post.setTags(tags);
        postService.createPost(post);
        return "redirect:/posts";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable int id, Principal principal, Model model) {
        Post post = postService.findPostById(id).orElseThrow();
        User loggedInUser = userService.findByEmail(principal.getName());

        if (!post.getAuthor().getEmail().equals(loggedInUser.getEmail()) &&
                !loggedInUser.getRole().equals("ROLE_ADMIN")) {
            return "redirect:/login";
        }

//        User loggedInUser = null;
//        if (principal != null) {
//            String email = principal.getName();  // Get the logged-in user's email
//            loggedInUser = userService.findByEmail(email); // Find the logged-in user
//        }

        // Convert tags to comma-separated string
        StringBuilder tagString = new StringBuilder();
        boolean first = true;

        for (Tag tag : post.getTags()) {
            if (!first) {
                tagString.append(", ");
            }
            tagString.append(tag.getName());
            first = false;
        }

        model.addAttribute("post", post);
        model.addAttribute("tagString", tagString.toString());
        return "posts/edit";
    }

     //Submit update
//    @PostMapping("/{id}/update")
//    public String updatePost(@PathVariable Long id, @ModelAttribute Post post) {
//        postService.updatePost(id, post);
//        return "redirect:/posts";
//    }



    @PostMapping("/{id}/update")
    public String updatePost(@PathVariable int id, @ModelAttribute Post post,
                             @RequestParam("tagString") String tagString,Principal principal) {


        String email = principal.getName();  // Get logged-in user's email
        User loggedInUser = userService.findByEmail(email);  // Use email to fetch logged-in user

        // Split the input string and create tag list
        List<String> tagNames = new ArrayList<>();
        if (tagString != null && !tagString.isEmpty()) {
            String[] parts = tagString.split(",");
            for (String part : parts) {
                String trimmedTag = part.trim();
                if (!trimmedTag.isEmpty()) {
                    tagNames.add(trimmedTag);
                }
            }
        }

        // Create Tag entities from the names
        Set<Tag> tags = new HashSet<>();
        for (String tagName : tagNames) {
            // Try to find existing tag
            Tag existingTag = tagRepository.findByName(tagName);

            Tag tag;
            if (existingTag != null) {
                // Use existing tag
                tag = existingTag;
            } else {
                // Create new tag
                Tag newTag = new Tag();
                newTag.setName(tagName);
                tag = tagRepository.save(newTag);
            }

            tags.add(tag);
        }

        post.setTags(tags);
        postService.updatePost(id, post);
        return "redirect:/posts";
    }



    // Delete post
    @PostMapping("/{id}/delete")
    public String deletePost(@PathVariable int id) {
        postService.deletePost(id);
        return "redirect:/posts";
    }
}
