package com.github.provitaliy.node.testConfig;

import com.github.provitaliy.common.messaging.QueueNames;
import com.github.provitaliy.common.messaging.RoutingKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@RequiredArgsConstructor
@TestConfiguration
public class TestRabbitConfig {
    private final DirectExchange mainExchange;

    @Bean
    public Queue answerMessageQueue() {
        return new Queue(QueueNames.ANSWER_MESSAGE_QUEUE);
    }

    @Bean
    public Binding answerMessageBinding() {
        return BindingBuilder
                .bind(answerMessageQueue())
                .to(mainExchange)
                .with(RoutingKeys.ROUTING_KEY_ANSWER_MESSAGE);
    }
}
