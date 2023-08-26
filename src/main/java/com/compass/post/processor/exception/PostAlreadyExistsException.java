package com.compass.post.processor.exception;

public class PostAlreadyExistsException extends RuntimeException {
    public PostAlreadyExistsException() {
        super();
    }

    public PostAlreadyExistsException(String message) {
        super(message);
    }

    public PostAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public PostAlreadyExistsException(Throwable cause) {
        super(cause);
    }    
}
