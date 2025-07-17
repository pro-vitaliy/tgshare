package com.github.provitaliy.service.impl;

import com.github.provitaliy.dto.MailParams;
import com.github.provitaliy.messaging.ExchangeNames;
import com.github.provitaliy.messaging.RoutingKeys;
import com.github.provitaliy.service.ProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Service
@RequiredArgsConstructor
public class ProducerServiceImpl implements ProducerService {
    private final RabbitTemplate rabbitTemplate;

    @Override
    public void produceAnswer(SendMessage sendMessage) {
        rabbitTemplate.convertAndSend(
                ExchangeNames.MAIN,
                RoutingKeys.ROUTING_KEY_ANSWER_MESSAGE,
                sendMessage
        );
    }

    @Override
    public void produceRegistrationMail(MailParams mailParams) {
        rabbitTemplate.convertAndSend(
                ExchangeNames.MAIN,
                RoutingKeys.ROUTING_KEY_EMAIL_MESSAGE,
                mailParams
        );
    }
}
