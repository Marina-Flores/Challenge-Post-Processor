package com.compass.post.processor.service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import com.compass.post.processor.entity.History;
import com.compass.post.processor.entity.Post;
import com.compass.post.processor.enums.PostState;
import com.compass.post.processor.exception.PostAlreadyExistsException;
import com.compass.post.processor.repository.PostRepository;

public class PostProcessingService {
    private final PostRepository postRepository;
    private final ApiService apiService;

    public PostProcessingService(
            PostRepository postRepository,
            ApiService apiService) {
        this.postRepository = postRepository;
        this.apiService = apiService;
    }

    public void processPost(Long id){
        // Iniciar Post com status Created
        List<History> histories = Arrays.asList(new History(PostState.CREATED, new Date()));

        // Verificar se Id do post já existe na base 
        Optional<Post> postOp = postRepository.findById(id);        

        if(postOp.isPresent()) throw new PostAlreadyExistsException("Post with ID " + id + " already exists in the database.");
        
        histories.add(new History(PostState.POST_FIND, new Date()));

        Post post = fetchPost(id);
        if(post.getId() == null) return;

        histories.add(new History(PostState.POST_OK, new Date()));
        
        post.setHistory(histories);
    }

    public Post fetchPost(Long id){
        try{
            return apiService.fetchPostById(id);
        }
        catch(Exception e){
            List<History> histories = Arrays.asList(new History(PostState.FAILED, new Date()));          
            Post post = new Post(histories);
            postRepository.save(post);

            return post;
        }
    }





}
