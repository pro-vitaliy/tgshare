package com.github.provitaliy.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

    @Value("${telegram.bot.token}")
    private String token;

    private final TelegramClient telegramClient;

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public void consume(Update update) {
        var origMessage = update.getMessage();
        log.debug(origMessage.getText());

        var response = SendMessage.builder()
                .chatId(update.getMessage().getChatId())
                .text("Hello from bot")
                .build();

        sendAnswerMessage(response);
    }

    public void sendAnswerMessage(SendMessage message) {
        if (message != null) {
            try {
                telegramClient.execute(message);
            } catch (TelegramApiException e) {
                log.error(e.getMessage());
            }
        }
    }
}
