package com.github.provitaliy.common.dto.telegram;

public record TelegramDocumentMessageDto(
        Long chatId,
        TelegramUserDto from,
        String documentId,
        String documentName,
        String mimeType,
        Long documentSize
) { }
