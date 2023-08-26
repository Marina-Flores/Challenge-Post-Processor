package com.compass.post.processor.service;

import java.util.Arrays;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.compass.post.processor.entity.Comment;
import com.compass.post.processor.entity.Post;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ApiService {

    private final RestTemplate restTemplate;
    private static String URL = "https://jsonplaceholder.typicode.com/posts/";

    public ApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Post fetchPostById(Long postId) throws JsonMappingException, JsonProcessingException {
        String response = restTemplate.getForObject(URL + postId, String.class);
        ObjectMapper mapper = new ObjectMapper();
        Post post = mapper.readValue(response, Post.class);
        return post;
    }

    public List<Comment> fetchCommentsByPost(Post post) {
        ResponseEntity<Comment[]> response = restTemplate.getForEntity(URL + post.getId() + "/comments",
                Comment[].class);
        Comment[] comments = response.getBody();
        post.setComments(Arrays.asList(comments));
        return Arrays.asList(comments);
    }
}
