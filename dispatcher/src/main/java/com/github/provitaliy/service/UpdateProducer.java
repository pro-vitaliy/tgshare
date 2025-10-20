package com.github.provitaliy.service;

import com.github.provitaliy.messaging.ExchangeNames;
import com.github.provitaliy.messaging.RoutingKeys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
@Slf4j
@RequiredArgsConstructor
public class UpdateProducer {
    private final RabbitTemplate rabbitTemplate;

    public void produceTextMessageUpdate(Update update) {
        rabbitTemplate.convertAndSend(
                ExchangeNames.MAIN,
                RoutingKeys.ROUTING_KEY_TEXT_MESSAGE_UPDATE,
                update
        );
    }

    public void produceDocMessageUpdate(Update update) {
        rabbitTemplate.convertAndSend(
                ExchangeNames.MAIN,
                RoutingKeys.ROUTING_KEY_DOC_MESSAGE_UPDATE,
                update
        );
    }

    public void producePhotoMessageUpdate(Update update) {
        rabbitTemplate.convertAndSend(
                ExchangeNames.MAIN,
                RoutingKeys.ROUTING_KEY_PHOTO_MESSAGE_UPDATE,
                update
        );
    }
}
