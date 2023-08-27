package com.compass.post.processor.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.compass.post.processor.dto.PostRequest;
import com.compass.post.processor.entity.ErrorResponse;
import com.compass.post.processor.entity.Post;
import com.compass.post.processor.service.ApiService;
import com.compass.post.processor.service.PostProcessingService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import java.sql.Timestamp;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;


@RestController
@RequestMapping("/api/post")
public class PostController {

    private ApiService apiService;
    private PostProcessingService postProcessingService;

    public PostController(ApiService apiService, PostProcessingService postProcessingService) {
        this.apiService = apiService;
        this.postProcessingService = postProcessingService;
    }

    @GetMapping("/{postId}")
    public Post getPostById(@PathVariable Long postId) {
        try {
            return apiService.fetchPostById(postId);
        } catch (JsonMappingException e) {
            e.printStackTrace();
            return null;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @PostMapping
    public ResponseEntity<?> processPost(@RequestBody PostRequest request) {
        try {
            postProcessingService.processPost(request);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception ex) {
            ErrorResponse error = new ErrorResponse(
                "Illegal Argument Exception: " + ex.getMessage(),
                new Timestamp(System.currentTimeMillis()), 
                HttpStatus.BAD_REQUEST.name()
            );

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<?> disablePost(@PathVariable Long postId){
        try{
            postProcessingService.disablePost(new PostRequest(postId));
            return new ResponseEntity<>(HttpStatus.OK);
        } catch(Exception ex){
            ErrorResponse error = new ErrorResponse(
                "Illegal Argument Exception: " + ex.getMessage(),
                new Timestamp(System.currentTimeMillis()), 
                HttpStatus.BAD_REQUEST.name()
            );

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
}
