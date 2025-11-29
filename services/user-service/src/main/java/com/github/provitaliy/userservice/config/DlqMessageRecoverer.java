package com.github.provitaliy.userservice.config;

import com.github.provitaliy.common.messaging.ExchangeNames;
import com.github.provitaliy.common.messaging.RoutingKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class DlqMessageRecoverer implements MessageRecoverer {
    private final RabbitTemplate rabbitTemplate;

    @Override
    public void recover(Message message, Throwable cause) {
        var messageProps = message.getMessageProperties();

        messageProps.setHeader("x-exception-class", cause.getClass().getSimpleName());
        messageProps.setHeader("x-exception-message", cause.getMessage());
        messageProps.setHeader("x-service", "user-service");
        messageProps.setHeader("x-original-exchange", messageProps.getReceivedExchange());
        messageProps.setHeader("x-original-routing-key", messageProps.getReceivedRoutingKey());

        rabbitTemplate.send(
                ExchangeNames.DLQ,
                RoutingKeys.ROUTING_KEY_USER_EMAIL_ENTERED_DLQ,
                message
        );
    }
}
