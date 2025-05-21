package com.github.provitaliy.service.impl;

import com.github.provitaliy.service.ConsumerService;
import com.github.provitaliy.service.ProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConsumerServiceImpl implements ConsumerService {
    private final ProducerService producerService;

    @Override
    @RabbitListener(queues = "${spring.rabbitmq.queues.text-message-update}")
    public void consumeTextMessageUpdates(Update update) {
        log.debug("NODE: Text message is received");

        SendMessage sendMessage = SendMessage.builder()
                .chatId(update.getMessage().getChatId())
                .text("Hello from NODE")
                .build();

        producerService.produceAnswer(sendMessage);
    }

    @Override
    @RabbitListener(queues = "${spring.rabbitmq.queues.doc-message-update}")
    public void consumeDocMessageUpdates(Update update) {
        log.debug("NODE: Doc message is received");
    }

    @Override
    @RabbitListener(queues = "${spring.rabbitmq.queues.photo-message-update}")
    public void consumePhotoMessageUpdates(Update update) {
        log.debug("NODE: Photo message is received");
    }
}
