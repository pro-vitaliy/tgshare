package com.github.provitaliy.service;

import com.github.provitaliy.dto.MailParams;
import com.github.provitaliy.messaging.ExchangeNames;
import com.github.provitaliy.messaging.RoutingKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Service
@RequiredArgsConstructor
public class ProducerService {
    private final RabbitTemplate rabbitTemplate;

    public void produceAnswer(SendMessage sendMessage) {
        rabbitTemplate.convertAndSend(
                ExchangeNames.MAIN,
                RoutingKeys.ROUTING_KEY_ANSWER_MESSAGE,
                sendMessage
        );
    }

    public void produceRegistrationMail(MailParams mailParams) {
        rabbitTemplate.convertAndSend(
                ExchangeNames.MAIN,
                RoutingKeys.ROUTING_KEY_EMAIL_MESSAGE,
                mailParams
        );
    }
}

