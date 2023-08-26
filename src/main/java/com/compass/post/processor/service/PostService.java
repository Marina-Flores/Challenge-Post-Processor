package com.compass.post.processor.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.compass.post.processor.entity.Post;
import com.compass.post.processor.repository.PostRepository;

@Service
public class PostService {

    private final PostRepository postRepository; 

    public PostService(PostRepository repository){
        this.postRepository = repository;
    }
    
    public List<Post> getAllPosts(){
        return postRepository.findAll();
    }
}
