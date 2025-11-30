package com.github.provitaliy.fileservice.service.event;

import com.github.provitaliy.common.event.FileReadyEvent;
import com.github.provitaliy.common.event.FileUploadFailedEvent;
import com.github.provitaliy.common.messaging.ExchangeNames;
import com.github.provitaliy.common.messaging.RoutingKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ProducerService {
    private final RabbitTemplate rabbitTemplate;

    public void produceFileReadyEvent(FileReadyEvent readyEvent) {
        rabbitTemplate.convertAndSend(
                ExchangeNames.MAIN,
                RoutingKeys.ROUTING_KEY_FILE_READY,
                readyEvent
        );
    }

    public void produceFileUploadFailedEvent(FileUploadFailedEvent failedEvent) {
        rabbitTemplate.convertAndSend(
                ExchangeNames.MAIN,
                RoutingKeys.ROUTING_KEY_FILE_UPLOAD_FAILED,
                failedEvent
        );
    }
}
