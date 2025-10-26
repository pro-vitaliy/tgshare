package com.github.provitaliy.userservice.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Long telegramUserId) {
        super("User not found for telegramUserId = " + telegramUserId);
    }
}
