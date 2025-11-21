package com.github.provitaliy.dispatcher.utils;

import lombok.experimental.UtilityClass;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@UtilityClass
public class MessageUtils {

    public static SendMessage generateSendMessageWithText(Update update, String text) {
        var message = update.getMessage();
        var chatId = message.getChatId();
        return SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .build();
    }
}
