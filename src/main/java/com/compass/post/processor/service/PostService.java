package com.compass.post.processor.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.compass.post.processor.entity.Post;
import com.compass.post.processor.repository.PostRepository;

@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) { this.postRepository = postRepository; }

    public Optional<Post> getPostById(Long id){
        return postRepository.findById(id);
    }

    public void insertPost(Post post){
        postRepository.save(post);
    }
   
}
