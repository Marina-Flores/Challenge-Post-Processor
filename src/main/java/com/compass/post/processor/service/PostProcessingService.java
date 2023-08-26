package com.compass.post.processor.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

import com.compass.post.processor.dto.PostRequest;
import com.compass.post.processor.entity.History;
import com.compass.post.processor.entity.Post;
import com.compass.post.processor.enums.PostState;
import com.compass.post.processor.exception.PostAlreadyExistsException;
import com.compass.post.processor.repository.PostRepository;

@Service
public class PostProcessingService {
    private final PostRepository postRepository;
    private final ApiService apiService;

    public PostProcessingService(
            PostRepository postRepository,
            ApiService apiService) {
        this.postRepository = postRepository;
        this.apiService = apiService;
    }

    public void processPost(PostRequest request) {
        // Start process with status Created
        List<History> histories = new ArrayList<>();
        histories.add(new History(PostState.CREATED, new Date()));

        // Check if the post id already exists in the database
        Optional<Post> postOp = postRepository.findById(request.id);

        if (postOp.isPresent())
            throw new PostAlreadyExistsException("Post with ID " + request.id + " already exists in the database.");

        histories.add(new History(PostState.POST_FIND, new Date()));

        Post post = fetchPost(request.id);
        if (post.getId() == null)
            return;

        histories.add(new History(PostState.POST_OK, new Date()));
        for (History history : histories) {
            history.setPost(post);
        }    
        post.setHistory(histories);  
        postRepository.save(post);
    }

    public Post fetchPost(Long id) {
        try {
            return apiService.fetchPostById(id);
        } catch (Exception e) {
            List<History> histories = Arrays.asList(new History(PostState.FAILED, new Date()));
            Post post = new Post(histories);
            postRepository.save(post);

            return post;
        }
    }

}
