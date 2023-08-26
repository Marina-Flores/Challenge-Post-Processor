package com.compass.post.processor.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.compass.post.processor.dto.PostRequest;
import com.compass.post.processor.entity.Post;
import com.compass.post.processor.service.ApiService;
import com.compass.post.processor.service.PostProcessingService;

@RestController
@RequestMapping("/api/post")
public class PostController {

    private ApiService apiService;
    private PostProcessingService postProcessingService;

    public PostController(ApiService apiService, PostProcessingService postProcessingService){ 
        this.apiService = apiService;
        this.postProcessingService = postProcessingService;
    }

    @GetMapping("/{postId}")
    public Post getPostById(@PathVariable Long postId) {
        return apiService.fetchPostById(postId);
    }    

    @PostMapping
    public void processPost(@RequestBody PostRequest request){
        postProcessingService.processPost(request);
    }
    
}
