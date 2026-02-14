package com.github.provitaliy.dispatcher.service;

import com.github.provitaliy.common.dto.telegram.SendMessageDto;
import com.github.provitaliy.common.dto.telegram.TelegramDocumentMessageDto;
import com.github.provitaliy.common.dto.telegram.TelegramPhotoMessageDto;
import com.github.provitaliy.common.dto.telegram.TelegramTextMessageDto;
import com.github.provitaliy.dispatcher.mapper.TelegramMessageMapper;
import com.github.provitaliy.dispatcher.utils.MessageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Slf4j
@RequiredArgsConstructor
@Service
public class UpdateService {
    private final TelegramClient telegramClient;
    private final UpdateProducer updateProducer;
    private final TelegramMessageMapper messageMapper;

    public void processUpdate(Update update) {
        if (update == null) {
            log.warn("Received null update");
            return;
        }

        if (!update.hasMessage()) {
            log.warn("Received update has no message: updateId={}", update.getUpdateId());
            return;
        }

        distributeMessagesByType(update);
    }

    public void sendMessage(SendMessageDto sendMessageDto) {
        sendMessage(messageMapper.toSendMessage(sendMessageDto));
    }

    private void sendMessage(SendMessage sendMessage) {
        try {
            telegramClient.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Failed to send message to chatId={}: {}",
                    sendMessage.getChatId(), e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error sending message to chatId={}: {}",
                    sendMessage.getChatId(), e.getMessage(), e);
        }
    }

    private void distributeMessagesByType(Update update) {
        var message = update.getMessage();

        if (message.hasText()) {
            log.debug("Received text message: messageId={}, chatId={}, userId={}",
                    message.getMessageId(), message.getChatId(), message.getFrom().getId());

            TelegramTextMessageDto textMessageDto = messageMapper.toTextMessageDto(message);
            updateProducer.produceTextMessageUpdate(textMessageDto);

        } else if (message.hasDocument()) {
            log.debug("Received document message: messageId={}, chatId={}, userId={}, documentId={}",
                    message.getMessageId(),
                    message.getChatId(),
                    message.getFrom().getId(),
                    message.getDocument().getFileId());

            TelegramDocumentMessageDto docMessageDto = messageMapper.toDocumentMessageDto(message);
            updateProducer.produceDocMessageUpdate(docMessageDto);

        } else if (message.hasPhoto()) {
            log.debug("Received photo message: messageId={}, chatId={}, userId={}, photoId={}",
                    message.getMessageId(),
                    message.getChatId(),
                    message.getFrom().getId(),
                    message.getPhoto().getLast().getFileId());

            TelegramPhotoMessageDto photoMessageDto = messageMapper.toPhotoMessageDto(message);
            updateProducer.producePhotoMessageUpdate(photoMessageDto);

        } else {
            log.warn("Unsupported message type for chatId={}, messageId={}, sending notification",
                    message.getChatId(),
                    message.getMessageId());
            var sendMessage = MessageUtils.generateUnsupportedTypeSendMessage(update);
            sendMessage(sendMessage);
        }
    }
}
