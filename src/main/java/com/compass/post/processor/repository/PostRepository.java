package com.compass.post.processor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.compass.post.processor.entity.Post;

public interface PostRepository extends JpaRepository<Post, Long> {}
