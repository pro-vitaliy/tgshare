package com.github.provitaliy.common.dto.telegram;

public record TelegramTextMessageDto(
        Long chatId,
        TelegramUserDto from,
        String text
) implements TelegramMessage { }
