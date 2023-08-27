package com.compass.post.processor.entity;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Post implements Cloneable {

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

    @Override
    public Object clone() throws CloneNotSupportedException {
        Post clonedPost = (Post) super.clone();
        clonedPost.setComments(new ArrayList<>(this.comments));
        clonedPost.setHistory(new ArrayList<>(this.history));
        return clonedPost;
    }
}
