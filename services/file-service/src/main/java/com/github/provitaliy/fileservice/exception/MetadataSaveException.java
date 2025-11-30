package com.github.provitaliy.fileservice.exception;

import lombok.Getter;

@Getter
public class MetadataSaveException extends RuntimeException {
    private final String objectName;

    public MetadataSaveException(String objectName, Throwable cause) {
        super("Ошибка при сохранении метаданных файла: objectName=" + objectName, cause);
        this.objectName = objectName;
    }
}
