package com.github.provitaliy.userservice.exception;

public class InvalidEncryptedIdException extends RuntimeException {
    public InvalidEncryptedIdException(String message) {
        super(message);
    }
}
