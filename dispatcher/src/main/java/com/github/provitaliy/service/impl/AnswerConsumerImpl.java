package com.github.provitaliy.service.impl;

import com.github.provitaliy.controller.UpdateController;
import com.github.provitaliy.service.AnswerConsumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Service
@Slf4j
@RequiredArgsConstructor
public class AnswerConsumerImpl implements AnswerConsumer {
    private final UpdateController updateController;

    @Override
    @RabbitListener(queues = "${spring.rabbitmq.queues.answer-message}")
    public void consume(SendMessage sendMessage) {
        updateController.setView(sendMessage);
    }
}
