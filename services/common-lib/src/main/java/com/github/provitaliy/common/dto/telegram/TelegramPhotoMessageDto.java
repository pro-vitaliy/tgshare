package com.github.provitaliy.common.dto.telegram;

public record TelegramPhotoMessageDto(
        Long chatId,
        TelegramUserDto from,
        String photoId,
        Long photoSize
) { }
