package com.compass.post.processor.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.compass.post.processor.entity.Post;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ApiService {
    
    private final RestTemplate restTemplate;
    private static String URL = "https://jsonplaceholder.typicode.com/posts/";
    
    public ApiService(RestTemplate restTemplate){ this.restTemplate = restTemplate; }

    public Post fetchPostById(Long postId){
        String response = restTemplate.getForObject(URL + postId, String.class);
        
        ObjectMapper mapper = new ObjectMapper();

        try{
            Post post = mapper.readValue(response, Post.class);  
            return post;          
        }
        catch(JsonProcessingException e){
            e.printStackTrace();
            return null;
        }  
    }
}
