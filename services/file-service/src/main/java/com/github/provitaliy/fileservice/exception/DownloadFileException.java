package com.github.provitaliy.fileservice.exception;

public class DownloadFileException extends RuntimeException {
    public DownloadFileException(String message) {
        super(message);
    }

    public DownloadFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
