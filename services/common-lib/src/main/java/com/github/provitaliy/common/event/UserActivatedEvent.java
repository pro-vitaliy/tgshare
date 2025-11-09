package com.github.provitaliy.common.event;

public record UserActivatedEvent(
        Long telegramUserId,
        String email
){ }
