package com.compass.post.processor.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

import com.compass.post.processor.dto.CommentResponse;
import com.compass.post.processor.dto.HistoryResponse;
import com.compass.post.processor.dto.PostResponse;
import com.compass.post.processor.entity.Comment;
import com.compass.post.processor.entity.History;
import com.compass.post.processor.entity.Post;
import com.compass.post.processor.repository.PostRepository;

@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public Optional<Post> getPostById(Long id) {
        return postRepository.findById(id);
    }

    public void insertPost(Post post) {
        postRepository.save(post);
    }

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public List<PostResponse> getAllPostsMapped() {
        List<Post> posts = postRepository.findAll();
        List<PostResponse> postResponses = new ArrayList<>();

        for (Post post : posts) {
            List<CommentResponse> commentResponses = mapComments(post.getComments());
            List<HistoryResponse> historyResponses = mapHistories(post.getHistory());

            PostResponse postResponse = new PostResponse(
                    post.getId(),
                    post.getUserId(),
                    post.getTitle(),
                    post.getBody(),
                    commentResponses,
                    historyResponses);

            postResponses.add(postResponse);
        }

        return postResponses;
    }

    private List<CommentResponse> mapComments(List<Comment> comments) {
        List<CommentResponse> commentResponses = new ArrayList<>();
        for (Comment comment : comments) {
            CommentResponse commentResponse = new CommentResponse(
                    comment.getId(),
                    comment.getName(),
                    comment.getEmail(),
                    comment.getBody());

            commentResponses.add(commentResponse);
        }
        return commentResponses;
    }

    private List<HistoryResponse> mapHistories(List<History> histories) {
        List<HistoryResponse> historyResponses = new ArrayList<>();
        for (History history : histories) {
            HistoryResponse historyResponse = new HistoryResponse(
                    history.getId(),
                    history.getDate(),
                    history.getStatus());
                    
            historyResponses.add(historyResponse);
        }
        return historyResponses;
    }

}
