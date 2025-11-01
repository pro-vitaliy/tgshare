package com.github.provitaliy.node.handler;

import com.github.provitaliy.common.dto.AppUserCreateDTO;
import org.apache.commons.validator.routines.EmailValidator;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.message.Message;

final class HandlerUtils {
    private static final EmailValidator EMAIL_VALIDATOR = EmailValidator.getInstance();

    private HandlerUtils() {};

    static AppUserCreateDTO buildUserCreateDto(Message telegramMessage) {
        User telegramUser = telegramMessage.getFrom();
        return AppUserCreateDTO.builder()
                .telegramUserId(telegramUser.getId())
                .chatId(telegramMessage.getChatId())
                .firstName(telegramUser.getFirstName())
                .lastName(telegramUser.getLastName())
                .username(telegramUser.getUserName())
                .build();
    }

    static SendMessage prepareMessage(String text, Long chatId) {
        return SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .build();
    }

    static boolean isValidEmail(String email) {
        if (email == null) return false;
        return EMAIL_VALIDATOR.isValid(email.trim().toLowerCase());
    }

    static String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }
}
