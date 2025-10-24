package com.github.provitaliy.dispatcher.utils;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class MessageUtils {

    public SendMessage generateSendMessageWithText(Update update, String text) {
        var message = update.getMessage();
        var chatId = message.getChatId();
        return SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .build();
    }
}
