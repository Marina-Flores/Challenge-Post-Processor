package com.compass.post.processor.entity;

import java.util.Date;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
public class History {    

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id; 

    @Temporal(TemporalType.TIMESTAMP)
    public Date date;

    public String status;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;
}
