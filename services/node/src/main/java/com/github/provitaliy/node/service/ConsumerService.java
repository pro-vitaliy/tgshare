package com.github.provitaliy.node.service;

import com.github.provitaliy.common.dto.telegram.TelegramDocumentMessageDto;
import com.github.provitaliy.common.dto.telegram.TelegramPhotoMessageDto;
import com.github.provitaliy.common.dto.telegram.TelegramTextMessageDto;
import com.github.provitaliy.common.event.FileReadyEvent;
import com.github.provitaliy.common.event.UserActivatedEvent;
import com.github.provitaliy.common.messaging.QueueNames;
import com.github.provitaliy.node.handler.FileUpdateHandler;
import com.github.provitaliy.node.handler.NodeUserEventHandler;
import com.github.provitaliy.node.handler.TextUpdateHandler;
import com.github.provitaliy.node.handler.UserNotificationEventHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ConsumerService {
    private final TextUpdateHandler textUpdateHandler;
    private final FileUpdateHandler fileUpdateHandler;
    private final UserNotificationEventHandler userNotificationEventHandler;
    private final NodeUserEventHandler userEventHandler;

    @RabbitListener(queues = QueueNames.TEXT_MESSAGE_UPDATE_QUEUE)
    public void consumeTextMessageUpdates(TelegramTextMessageDto textMessage) {
        log.debug("NODE: Text message is received");
        textUpdateHandler.handleUpdate(textMessage);
    }

    @RabbitListener(queues = QueueNames.DOC_MESSAGE_UPDATE_QUEUE)
    public void consumeDocMessageUpdates(TelegramDocumentMessageDto docMessage) {
        log.debug("NODE: Doc message is received");
        fileUpdateHandler.handleDocUpdate(docMessage);
    }

    @RabbitListener(queues = QueueNames.PHOTO_MESSAGE_UPDATE_QUEUE)
    public void consumePhotoMessageUpdates(TelegramPhotoMessageDto photoMessage) {
        log.debug("NODE: Photo message is received");
        fileUpdateHandler.handlePhotoUpdate(photoMessage);
    }

    @RabbitListener(queues = QueueNames.FILE_READY_QUEUE)
    public void consumeFileReadyEvent(FileReadyEvent event) {
        userNotificationEventHandler.handleFileReadyEvent(event.telegramUserId(), event.fileUrl());
    }

    @RabbitListener(queues = QueueNames.USER_ACTIVATED_QUEUE)
    public void consumeUserActivationEvent(UserActivatedEvent event) {
        userEventHandler.userActivatedEventHandler(event);
//        TODO: опубликовать возможные исключение в DLQ
    }
}
