package com.github.provitaliy.dispatcher.service;

import com.github.provitaliy.common.dto.telegram.TelegramDocumentMessageDto;
import com.github.provitaliy.common.dto.telegram.TelegramPhotoMessageDto;
import com.github.provitaliy.common.dto.telegram.TelegramTextMessageDto;
import com.github.provitaliy.common.messaging.ExchangeNames;
import com.github.provitaliy.common.messaging.RoutingKeys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class UpdateProducer {

    private final RabbitTemplate rabbitTemplate;

    public void produceTextMessageUpdate(TelegramTextMessageDto textMessage) {
        rabbitTemplate.convertAndSend(
                ExchangeNames.MAIN,
                RoutingKeys.ROUTING_KEY_TEXT_MESSAGE_UPDATE,
                textMessage
        );
    }

    public void produceDocMessageUpdate(TelegramDocumentMessageDto docMessage) {
        rabbitTemplate.convertAndSend(
                ExchangeNames.MAIN,
                RoutingKeys.ROUTING_KEY_DOC_MESSAGE_UPDATE,
                docMessage
        );
    }

    public void producePhotoMessageUpdate(TelegramPhotoMessageDto photoMessage) {
        rabbitTemplate.convertAndSend(
                ExchangeNames.MAIN,
                RoutingKeys.ROUTING_KEY_PHOTO_MESSAGE_UPDATE,
                photoMessage
        );
    }
}
