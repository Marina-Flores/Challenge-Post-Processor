package com.compass.post.processor.entity;

import java.util.Date;

import com.compass.post.processor.enums.PostState;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class History {    

    public History(PostState state, Date date, Post post) {
        this.date = date; 
        this.status = state;
        this.post = post;
    }

    public History(PostState state, Date date) {
        this.date = date; 
        this.status = state;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id; 

    @Temporal(TemporalType.TIMESTAMP)
    public Date date;

    public PostState status;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;
}
