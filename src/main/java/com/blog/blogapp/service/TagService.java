package com.blog.blogapp.service;
import com.blog.blogapp.entity.Tag;
import com.blog.blogapp.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TagService {
    @Autowired
    private TagRepository tagRepository;

    public Tag createTag(Tag tag) {
        return tagRepository.save(tag);
    }

    public Tag findByName(String name) {
        return tagRepository.findByName(name);
    }
}
