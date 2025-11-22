package com.github.provitaliy.node.exception;

public class RetryableGrpcException extends RuntimeException {
    public RetryableGrpcException(String message) {
        super(message);
    }
}
