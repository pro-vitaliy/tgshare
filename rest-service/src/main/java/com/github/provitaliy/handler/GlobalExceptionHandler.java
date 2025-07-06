package com.github.provitaliy.handler;

import com.github.provitaliy.exception.InvalidEncryptedIdException;
import com.github.provitaliy.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException e) {
        log.warn("Пользователь не найден, {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Такого пользователя не существует.");
    }

    @ExceptionHandler(InvalidEncryptedIdException.class)
    public ResponseEntity<String> handleInvalidEncryptedIdException(InvalidEncryptedIdException e) {
        log.warn("Не удалось расщифровать id, {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ссылка недействительна или устарела");
    }
}
