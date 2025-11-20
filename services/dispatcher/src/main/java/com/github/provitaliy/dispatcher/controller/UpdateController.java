package com.github.provitaliy.dispatcher.controller;

import com.github.provitaliy.common.dto.telegram.SendMessageDto;
import com.github.provitaliy.common.dto.telegram.TelegramDocumentMessageDto;
import com.github.provitaliy.common.dto.telegram.TelegramPhotoMessageDto;
import com.github.provitaliy.common.dto.telegram.TelegramTextMessageDto;
import com.github.provitaliy.dispatcher.mapper.TelegramMessageMapper;
import com.github.provitaliy.dispatcher.service.UpdateProducer;
import com.github.provitaliy.dispatcher.utils.MessageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Slf4j
@RequiredArgsConstructor
@Component
public class UpdateController {
    private final TelegramClient telegramClient;
    private final UpdateProducer updateProducer;
    private final MessageUtils messageUtils;
    private final TelegramMessageMapper messageMapper;

    public void processUpdate(Update update) {
        if (update == null) {
            log.error("Received update is null");
            return;
        }

        if (update.hasMessage()) {
            distributeMessagesByType(update);
        } else {
            log.error("Unsupported update type {}", update);
            setUnsupportedTypeView(update);
        }
    }

    private void distributeMessagesByType(Update update) {
        var message = update.getMessage();

        if (message.hasText()) {
            TelegramTextMessageDto textMessageDto = messageMapper.toTextMessageDto(message);
            updateProducer.produceTextMessageUpdate(textMessageDto);
        } else if (message.hasDocument()) {
            TelegramDocumentMessageDto docMessageDto = messageMapper.toDocumentMessageDto(message);
            updateProducer.produceDocMessageUpdate(docMessageDto);
        } else if (message.hasPhoto()) {
            TelegramPhotoMessageDto photoMessageDto = messageMapper.toPhotoMessageDto(message);
            updateProducer.producePhotoMessageUpdate(photoMessageDto);
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

    public void setView(SendMessageDto sendMessageDto) {
        setView(messageMapper.toSendMessage(sendMessageDto));
    }
}
