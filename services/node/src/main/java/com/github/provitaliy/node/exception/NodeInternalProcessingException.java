package com.github.provitaliy.node.exception;

public class NodeInternalProcessingException extends RuntimeException {
    public NodeInternalProcessingException(String message) {
        super(message);
    }

    public NodeInternalProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
