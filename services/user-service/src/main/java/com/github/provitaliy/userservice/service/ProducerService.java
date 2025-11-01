package com.github.provitaliy.userservice.service;

import com.github.provitaliy.common.event.SendEmailEvent;
import com.github.provitaliy.common.messaging.ExchangeNames;
import com.github.provitaliy.common.messaging.RoutingKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ProducerService {
    private final RabbitTemplate rabbitTemplate;

    public void produceSendMailEvent(SendEmailEvent emailEvent) {
        rabbitTemplate.convertAndSend(
                ExchangeNames.MAIN,
                RoutingKeys.ROUTING_KEY_EMAIL_SEND,
                emailEvent
        );
    }

}
