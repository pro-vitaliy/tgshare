package com.github.provitaliy.service.impl;

import com.github.provitaliy.dto.MailParams;
import com.github.provitaliy.service.ProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Service
@RequiredArgsConstructor
public class ProducerServiceImpl implements ProducerService {

    @Value("${spring.rabbitmq.queues.answer-message}")
    private String answerQueue;

    @Value("${spring.rabbitmq.queues.registration-mail}")
    private String emailRegisterQueue;

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void produceAnswer(SendMessage sendMessage) {
        rabbitTemplate.convertAndSend(answerQueue, sendMessage);
    }

    @Override
    public void produceRegistrationMail(MailParams mailParams) {
        rabbitTemplate.convertAndSend(emailRegisterQueue, mailParams);
    }
}
