package com.github.provitaliy.common.event;

public record FileReadyEvent(
        Long telegramUserId,
        String fileUrl
) { }
