package com.github.provitaliy.node.service;

import com.github.provitaliy.common.event.UserEmailEnteredEvent;
import com.github.provitaliy.common.messaging.ExchangeNames;
import com.github.provitaliy.common.messaging.RoutingKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@RequiredArgsConstructor
@Service
public class ProducerService {
    private final RabbitTemplate rabbitTemplate;

    public void produceAnswer(SendMessage sendMessage) {
        rabbitTemplate.convertAndSend(
                ExchangeNames.MAIN,
                RoutingKeys.ROUTING_KEY_ANSWER_MESSAGE,
                sendMessage
        );
    }

    public void produceRegistrationMail(UserEmailEnteredEvent emailEnteredEvent) {
        rabbitTemplate.convertAndSend(
                ExchangeNames.MAIN,
                RoutingKeys.ROUTING_KEY_USER_EMAIL_ENTERED,
                emailEnteredEvent
        );
    }
}
