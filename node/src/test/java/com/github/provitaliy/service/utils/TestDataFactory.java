package com.github.provitaliy.service.utils;

import com.github.provitaliy.entity.AppUser;
import com.github.provitaliy.entity.enums.UserState;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.message.Message;

public class TestDataFactory {

    public static Update createUpdateWithTextMessage(Long tgUserId, String text, Long tgChatId) {
        User telegramUser = User.builder()
                .id(tgUserId)
                .userName("username")
                .firstName("Firstname")
                .lastName("Lastname")
                .isBot(false)
                .build();

        Update update = new Update();
        Message message = new Message();
        message.setFrom(telegramUser);
        message.setChat(new Chat(tgChatId, "defaultType"));
        message.setText(text);
        update.setMessage(message);
        return update;
    }

    public static Update createUpdateWithTextMessage(Long tgUserId, String text) {
        Long defaultChatId = 123L;
        return createUpdateWithTextMessage(tgUserId, text, defaultChatId);
    }

    public static Update createUpdateWithDocument(Long chatId, Long docId) {
        Update update = new Update();
        Message message = new Message();
        Document document = Document.builder()
                .fileName("document.txt")
                .fileId(String.valueOf(docId))
                .mimeType("text/plain")
                .build();

        return update;
    }

    public static AppUser createRegisteredAppUser(Long tgUserId, String email, UserState state) {
        return AppUser.builder()
                .telegramUserId(tgUserId)
                .email(email)
                .userState(state)
                .isActive(true)
                .build();
    }

    public static AppUser createNotRegisteredAppUser(Long tgUserId, UserState state) {
        return AppUser.builder()
                .telegramUserId(tgUserId)
                .userState(state)
                .isActive(false)
                .build();
    }
}
