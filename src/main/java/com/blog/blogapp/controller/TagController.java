package com.blog.blogapp.controller;
import com.blog.blogapp.entity.Tag;
import com.blog.blogapp.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/tags")
public class TagController {
    @Autowired
    private TagService tagService;

    @GetMapping("/create")
    public String showCreateTagForm(Model model) {
        model.addAttribute("tag", new Tag());
        return "tags/create";
    }

    @PostMapping("/create")
    public String createTag(@ModelAttribute Tag tag) {
        tagService.createTag(tag);
        return "redirect:/posts";
    }
}
