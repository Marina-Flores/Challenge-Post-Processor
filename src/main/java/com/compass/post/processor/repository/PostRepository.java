package com.compass.post.processor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.compass.post.processor.entity.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> { }
