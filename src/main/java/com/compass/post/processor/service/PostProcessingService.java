package com.compass.post.processor.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Service;
import com.compass.post.processor.dto.PostRequest;
import com.compass.post.processor.entity.Comment;
import com.compass.post.processor.entity.History;
import com.compass.post.processor.entity.Post;
import com.compass.post.processor.enums.PostState;
import com.compass.post.processor.exception.IllegalArgumentException;
import com.compass.post.processor.exception.PostAlreadyExistsException;
import com.compass.post.processor.exception.PostNotFoundException;
import com.compass.post.processor.repository.PostRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

@Service
public class PostProcessingService {
    private final PostRepository postRepository;
    private final ApiService apiService;

    public PostProcessingService(
            PostRepository postRepository,
            ApiService apiService) {
        this.postRepository = postRepository;
        this.apiService = apiService;
    }

    public void processPost(PostRequest request, boolean isReprocess)
            throws CloneNotSupportedException, JsonMappingException, JsonProcessingException {

        List<History> histories = new ArrayList<>();

        if (!isReprocess) {
            validatePostId(request);

            // Start process with status Created
            histories.add(new History(PostState.CREATED, new Date()));

            // Check if the post id already exists in the database
            Optional<Post> postOp = postRepository.findById(request.id);

            if (postOp.isPresent())
                throw new PostAlreadyExistsException("Post with ID " + request.id + " already exists in the database.");
        }

        // Retrieving Post History
        histories.add(new History(PostState.POST_FIND, new Date()));

        Post post = apiService.fetchPostById(request.id);
        if (post.getId() == null)
            return;

        histories.add(new History(PostState.POST_OK, new Date()));

        post.setHistory(histories);

        for (History history : histories) {
            history.setPost(post);
        }

        // Retrieving Post Comments
        post = (Post) retrievePostWithComments(post).clone();

        postRepository.save(post);
    }

    public void disablePost(PostRequest request) {
        validatePostId(request);

        Optional<Post> postOp = postRepository.findById(request.id);

        if (!postOp.isPresent())
            throw new PostNotFoundException("Post with ID " + request.id + " does not exists in the database.");

        Post post = postOp.get();
        PostState currentState = post.getHistory().get(post.getHistory().size() - 1).getStatus();

        if (currentState != PostState.ENABLED)
            throw new IllegalArgumentException("Post with ID " + request.id
                    + " needs to have the current status ENABLED in order to be DISABLED.");

        post.getHistory().add(new History(PostState.DISABLED, new Date(), post));

        postRepository.save(post);
    }

    public void reprocessPost(PostRequest request) throws CloneNotSupportedException, JsonMappingException, JsonProcessingException {
        List<History> histories = new ArrayList<>();
        histories.add(new History(PostState.UPDATING, new Date()));

        validatePostId(request);

        // Check if the post id already exists in the database
        Optional<Post> postOp = postRepository.findById(request.id);

        if (!postOp.isPresent())
            throw new PostNotFoundException("Post with ID " + request.id + " does not exists in the database.");

        Post post = (Post) postOp.get().clone();

        for (History history : histories) {
            history.setPost(post);
        }
        post.setComments(null);
        postRepository.save(post);

        processPost(request, true);
    }

    private Post retrievePostWithComments(Post post) {
        post.getHistory().add(new History(PostState.COMMENTS_FIND, new Date(), post));

        List<Comment> comments = getCommentsByPost(post);

        if (comments != null) {
            for (Comment comment : comments) {
                comment.setPost(post);
            }
            post.setComments(comments);
        }

        post.getHistory().add(new History(PostState.COMMENTS_OK, new Date(), post));
        post.getHistory().add(new History(PostState.ENABLED, new Date(), post));

        return post;
    }

    private void validatePostId(PostRequest request) {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<PostRequest>> violations = validator.validate(request);

        if (!violations.isEmpty()) {
            List<String> errorMessages = new ArrayList<>();
            for (ConstraintViolation<PostRequest> violation : violations) {
                errorMessages.add(violation.getMessage());
            }
            String errorMessage = String.join(", ", errorMessages);
            throw new IllegalArgumentException(errorMessage);
        }
    }

    private List<Comment> getCommentsByPost(Post post) {
        try {
            return apiService.fetchCommentsByPost(post);
        } catch (Exception e) {
            post.getHistory().add(new History(PostState.FAILED, new Date()));
            post.getHistory().add(new History(PostState.DISABLED, new Date()));
            postRepository.save(post);
            return null;
        }
    }
}
