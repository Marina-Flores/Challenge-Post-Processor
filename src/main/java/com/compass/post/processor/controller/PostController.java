package com.compass.post.processor.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.compass.post.processor.entity.Post;
import com.compass.post.processor.service.ApiService;

@RestController
@RequestMapping("/api/post")
public class PostController {

    private ApiService apiService;

    public PostController(ApiService apiService){ this.apiService = apiService; }

    @GetMapping("/{postId}")
    public Post getPostById(@PathVariable Long postId) {
        return apiService.fetchPostById(postId);
    }    
}
