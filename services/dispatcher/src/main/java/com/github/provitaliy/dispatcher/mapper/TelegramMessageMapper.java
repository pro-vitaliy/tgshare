package com.github.provitaliy.dispatcher.mapper;

import com.github.provitaliy.common.dto.telegram.SendMessageDto;
import com.github.provitaliy.common.dto.telegram.TelegramDocumentMessageDto;
import com.github.provitaliy.common.dto.telegram.TelegramPhotoMessageDto;
import com.github.provitaliy.common.dto.telegram.TelegramTextMessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.photo.PhotoSize;

@RequiredArgsConstructor
@Component
public class TelegramMessageMapper {
    private final TelegramUserMapper telegramUserMapper;

    public TelegramTextMessageDto toTextMessageDto(Message message) {
        if (message == null) {
            return null;
        }

        return new TelegramTextMessageDto(
                message.getChatId(),
                telegramUserMapper.toDto(message.getFrom()),
                message.getText()
        );
    }

    public TelegramDocumentMessageDto toDocumentMessageDto(Message message) {
        if (message == null) {
            return null;
        }

        return new TelegramDocumentMessageDto(
                message.getChatId(),
                telegramUserMapper.toDto(message.getFrom()),
                message.getDocument().getFileId(),
                message.getDocument().getFileName(),
                message.getDocument().getMimeType(),
                message.getDocument().getFileSize()
        );
    }

    public TelegramPhotoMessageDto toPhotoMessageDto(Message message) {
        if (message == null) {
            return null;
        }

        PhotoSize photoSize = message.getPhoto().getLast();
        return new TelegramPhotoMessageDto(
                message.getChatId(),
                telegramUserMapper.toDto(message.getFrom()),
                photoSize.getFileId(),
                photoSize.getFileSize().longValue()
        );
    }

    public SendMessage toSendMessage(SendMessageDto sendMessageDto) {
        if (sendMessageDto == null) {
            return null;
        }

        return SendMessage.builder()
                .chatId(sendMessageDto.chatId())
                .text(sendMessageDto.text())
                .build();
    }
}
