package com.compass.post.processor.entity;

import java.util.Date;
import com.compass.post.processor.enums.PostState;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class ProcessLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id; 
    public Date date;  
    public PostState state;
}
