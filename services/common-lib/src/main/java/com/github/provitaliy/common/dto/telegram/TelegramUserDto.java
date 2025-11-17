package com.github.provitaliy.common.dto.telegram;

public record TelegramUserDto(
        Long id,
        String firstName,
        String lastName,
        String username
) { }
