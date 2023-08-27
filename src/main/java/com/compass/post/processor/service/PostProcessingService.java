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

    public void processPost(PostRequest request) {
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

        // Start process with status Created
        List<History> histories = new ArrayList<>();
        histories.add(new History(PostState.CREATED, new Date()));

        // Check if the post id already exists in the database
        Optional<Post> postOp = postRepository.findById(request.id);

        if (postOp.isPresent())
            throw new PostAlreadyExistsException("Post with ID " + request.id + " already exists in the database.");

        histories.add(new History(PostState.POST_FIND, new Date()));

        Post post = fetchPost(request.id);
        if (post.getId() == null)
            return;

        histories.add(new History(PostState.POST_OK, new Date()));

        post.setHistory(histories);

        for (History history : histories) {
            history.setPost(post);
        }

        // Retrieving Post Comments
        post.getHistory().add(new History(PostState.COMMENTS_FIND, new Date(), post));

        List<Comment> comments = fetchComments(post);

        if (comments != null) {
            for (Comment comment : comments) {
                comment.setPost(post);
            }

            post.setComments(comments);
            post.getHistory().add(new History(PostState.COMMENTS_OK, new Date(), post));
            post.getHistory().add(new History(PostState.ENABLED, new Date(), post));

            postRepository.save(post);
        }
    }

    public void disablePost(PostRequest request) {
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

         // Check if the post id already exists in the database
        Optional<Post> postOp = postRepository.findById(request.id);

        if (!postOp.isPresent())
            throw new PostNotFoundException("Post with ID " + request.id + " does not exists in the database.");

        Post post = postOp.get();    

        if(post.getHistory().get(post.getHistory().size() - 1).getStatus() != PostState.ENABLED)
            throw new IllegalArgumentException("Post with ID " + request.id + " needs to have the current status ENABLED in order to be DISABLED.");

        post.getHistory().add(new History(PostState.DISABLED, new Date(), post));

        postRepository.save(post);
    }

    public void reprocessPost(PostRequest request) throws CloneNotSupportedException{
        List<History> histories = new ArrayList<>();
        histories.add(new History(PostState.UPDATING, new Date()));

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

        // Check if the post id already exists in the database
        Optional<Post> postOp = postRepository.findById(request.id);

        if (!postOp.isPresent())
            throw new PostNotFoundException("Post with ID " + request.id + " does not exists in the database.");

        Post post = (Post) postOp.get().clone();
    
        histories.add(new History(PostState.POST_FIND, new Date()));

        PostState currentState = post.getHistory().get(post.getHistory().size() - 1).getStatus();

        if(currentState != PostState.ENABLED && currentState != PostState.DISABLED)
            throw new IllegalArgumentException("Post with ID " + request.id + " needs to have the current status ENABLED or DISABLED in order to be REPROCESSED.");

        histories.add(new History(PostState.POST_OK, new Date()));
        
         for (History history : histories) {
            history.setPost(post);
        }

        post.setHistory(histories);

        // Retrieving Post Comments
        post.getHistory().add(new History(PostState.COMMENTS_FIND, new Date(), post));

        List<Comment> comments = fetchComments(post);

        if (comments != null) {
            for (Comment comment : comments) {
                comment.setPost(post);
            }

            post.setComments(comments);
            post.getHistory().add(new History(PostState.COMMENTS_OK, new Date(), post));
            post.getHistory().add(new History(PostState.ENABLED, new Date(), post));

            postRepository.save(post);
        }
    }

    public Post fetchPost(Long id) {
        try {
            return apiService.fetchPostById(id);
        } catch (Exception e) {
            return null;
        }
    }

    public List<Comment> fetchComments(Post post) {
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
