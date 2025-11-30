package com.github.provitaliy.common.event;

public record FileUploadFailedEvent(
        Long telegramUserId,
        String fileName
) {
}
