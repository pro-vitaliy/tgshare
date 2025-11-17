package com.github.provitaliy.common.dto.telegram;

public record SendMessageDto(
        Long chatId,
        String text
) {
}
