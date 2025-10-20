package com.github.provitaliy.service;

import com.github.provitaliy.controller.UpdateController;
import com.github.provitaliy.messaging.QueueNames;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Service
@Slf4j
@RequiredArgsConstructor
public class AnswerConsumer {
    private final UpdateController updateController;

    @RabbitListener(queues = QueueNames.ANSWER_MESSAGE_QUEUE)
    public void consume(SendMessage sendMessage) {
        updateController.setView(sendMessage);
    }
}