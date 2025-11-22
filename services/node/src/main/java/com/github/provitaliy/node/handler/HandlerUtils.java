package com.github.provitaliy.node.handler;

import com.github.provitaliy.common.dto.AppUserCreateDTO;
import com.github.provitaliy.common.dto.telegram.TelegramMessage;
import com.github.provitaliy.common.dto.telegram.TelegramUserDto;
import lombok.experimental.UtilityClass;
import org.apache.commons.validator.routines.EmailValidator;

@UtilityClass
final class HandlerUtils {
    private static final EmailValidator EMAIL_VALIDATOR = EmailValidator.getInstance();

    static AppUserCreateDTO buildUserCreateDto(TelegramMessage telegramMessage) {
        TelegramUserDto telegramUser = telegramMessage.from();
        return AppUserCreateDTO.builder()
                .telegramUserId(telegramUser.id())
                .chatId(telegramMessage.chatId())
                .firstName(telegramUser.firstName())
                .lastName(telegramUser.lastName())
                .username(telegramUser.username())
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
