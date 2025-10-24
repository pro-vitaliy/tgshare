package com.github.provitaliy.dispatcher.service;

import com.github.provitaliy.common.messaging.ExchangeNames;
import com.github.provitaliy.common.messaging.RoutingKeys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@RequiredArgsConstructor
@Service
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
