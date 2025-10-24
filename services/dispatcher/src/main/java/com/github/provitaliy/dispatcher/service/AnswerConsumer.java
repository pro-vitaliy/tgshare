package com.github.provitaliy.dispatcher.service;

import com.github.provitaliy.common.messaging.QueueNames;
import com.github.provitaliy.dispatcher.controller.UpdateController;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@RequiredArgsConstructor
@Service
public class AnswerConsumer {

    private final UpdateController updateController;

    @RabbitListener(queues = QueueNames.ANSWER_MESSAGE_QUEUE)
    public void consume(SendMessage sendMessage) {
        updateController.setView(sendMessage);
    }
}
