package com.github.provitaliy.controller;

import com.github.provitaliy.configuration.QueueProperties;
import com.github.provitaliy.service.UpdateProducer;
import com.github.provitaliy.utils.MessageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
@Slf4j
@RequiredArgsConstructor
public class UpdateController {
    private final MessageUtils messageUtils;
    private final TelegramClient telegramClient;
    private final UpdateProducer updateProducer;
    private final QueueProperties queueProperties;

    public void processUpdate(Update update) {
        if (update == null) {
            log.error("Received update is null");
            return;
        }

        if (update.hasMessage()) {
            distributeMessagesByType(update);
        } else {
            log.error("Unsupported update type {}", update);
        }
    }

    private void distributeMessagesByType(Update update) {
        var message = update.getMessage();
        if (message.hasText()) {
            updateProducer.produce(queueProperties.getTextMessageUpdate(), update);
        } else if (message.hasDocument()) {
            updateProducer.produce(queueProperties.getDocMessageUpdate(), update);
        } else if (message.hasPhoto()) {
            updateProducer.produce(queueProperties.getPhotoMessageUpdate(), update);
        } else {
            setUnsupportedTypeView(update);
        }
    }

    private void setUnsupportedTypeView(Update update) {
        var sendMessage = messageUtils.generateSendMessageWithText(update, "Неподдерживаемый тип сообщения");
        setView(sendMessage);
    }

    public void setView(SendMessage sendMessage) {
        try {
            telegramClient.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Error sending message", e);
        }
    }
}
