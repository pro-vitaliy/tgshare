package com.github.provitaliy.node.exception;

public class UserServiceProcessingException extends RuntimeException {
    public UserServiceProcessingException(String message) {
        super(message);
    }

    public UserServiceProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
