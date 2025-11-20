package com.github.provitaliy.common.dto.telegram;

public interface TelegramMessage {
    Long chatId();
    TelegramUserDto from();
}
