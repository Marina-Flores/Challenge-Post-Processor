package com.compass.post.processor.entity;

import java.util.List;
import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Post {

    public Post(List<History> history) {
        this.history = history;
    }

    public Post(Long id, List<History> history) {
        this.id = id;
        this.history = history;
    }

    public Post(Long id) {
        this.id = id;
    }

    @Id
    public Long id;

    public int userId;

    public String title;

    public String body;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<Comment> comments;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<History> history;
}
