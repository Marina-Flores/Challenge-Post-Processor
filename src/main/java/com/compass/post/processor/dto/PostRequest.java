package com.compass.post.processor.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class PostRequest {
    public PostRequest() {}
    public PostRequest(Long id) { this.id = id; }
    
    @Min(value = 1, message = "Port ID must be a number between 1 and 100")
    @Max(value = 100, message = "Port ID must be a number between 1 and 100")
    public Long id;
}
