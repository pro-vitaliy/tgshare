package com.github.provitaliy.dispatcher.util;

import com.github.provitaliy.common.dto.telegram.TelegramDocumentMessageDto;
import com.github.provitaliy.common.dto.telegram.TelegramPhotoMessageDto;
import com.github.provitaliy.common.dto.telegram.TelegramTextMessageDto;
import com.github.provitaliy.common.dto.telegram.TelegramUserDto;
import lombok.experimental.UtilityClass;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.photo.PhotoSize;

@UtilityClass
public class TestDtoFactory {
    public static TelegramTextMessageDto textDtoFrom(Update update) {
        Message msg = update.getMessage();
        TelegramUserDto user = userDtoFrom(msg.getFrom());
        return new TelegramTextMessageDto(
                msg.getChatId(),
                user,
                msg.getText()
        );
    }

    public static TelegramDocumentMessageDto docDtoFrom(Update update) {
        Message msg = update.getMessage();
        TelegramUserDto user = userDtoFrom(msg.getFrom());
        return new TelegramDocumentMessageDto(
                msg.getChatId(),
                user,
                msg.getDocument().getFileId(),
                msg.getDocument().getFileName(),
                msg.getDocument().getMimeType(),
                msg.getDocument().getFileSize()
        );
    }

    public static TelegramPhotoMessageDto photoDtoFrom(Update update) {
        Message msg = update.getMessage();
        TelegramUserDto user = userDtoFrom(msg.getFrom());
        PhotoSize photoSize = msg.getPhoto().getLast();
        return new TelegramPhotoMessageDto(
                msg.getChatId(),
                user,
                photoSize.getFileId(),
                photoSize.getFileSize().longValue()
        );
    }

    private static TelegramUserDto userDtoFrom(User tgUser) {
        return new TelegramUserDto(
                tgUser.getId(),
                tgUser.getFirstName(),
                tgUser.getLastName(),
                tgUser.getUserName()
        );
    }
}
