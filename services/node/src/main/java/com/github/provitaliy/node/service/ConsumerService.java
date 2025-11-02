package com.github.provitaliy.node.service;

import com.github.provitaliy.common.messaging.QueueNames;
import com.github.provitaliy.node.handler.FileUpdateHandler;
import com.github.provitaliy.node.handler.TextUpdateHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@RequiredArgsConstructor
@Service
public class ConsumerService {
    private final TextUpdateHandler textUpdateHandler;
    private final FileUpdateHandler fileUpdateHandler;

    @RabbitListener(queues = QueueNames.TEXT_MESSAGE_UPDATE_QUEUE)
    public void consumeTextMessageUpdates(Update update) {
        log.debug("NODE: Text message is received");
        textUpdateHandler.handleUpdate(update);
    }

    @RabbitListener(queues = QueueNames.DOC_MESSAGE_UPDATE_QUEUE)
    public void consumeDocMessageUpdates(Update update) {
        log.debug("NODE: Doc message is received");
        fileUpdateHandler.handleDocUpdate(update);
    }

    @RabbitListener(queues = QueueNames.PHOTO_MESSAGE_UPDATE_QUEUE)
    public void consumePhotoMessageUpdates(Update update) {
        log.debug("NODE: Photo message is received");
        fileUpdateHandler.handlePhotoUpdate(update);
    }
}
