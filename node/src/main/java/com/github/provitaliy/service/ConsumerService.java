package com.github.provitaliy.service;

import com.github.provitaliy.messaging.QueueNames;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConsumerService {
    private final MainService mainService;

    @RabbitListener(queues = QueueNames.TEXT_MESSAGE_UPDATE_QUEUE)
    public void consumeTextMessageUpdates(Update update) {
        log.debug("NODE: Text message is received");

        mainService.processTextMessage(update);
    }

    @RabbitListener(queues = QueueNames.DOC_MESSAGE_UPDATE_QUEUE)
    public void consumeDocMessageUpdates(Update update) {
        log.debug("NODE: Doc message is received");

        mainService.processDocMessage(update);
    }

    @RabbitListener(queues = QueueNames.PHOTO_MESSAGE_UPDATE_QUEUE)
    public void consumePhotoMessageUpdates(Update update) {
        log.debug("NODE: Photo message is received");
        mainService.processPhotoMessage(update);
    }
}
