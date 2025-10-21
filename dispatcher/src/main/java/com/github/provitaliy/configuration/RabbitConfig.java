package com.github.provitaliy.configuration;

import com.github.provitaliy.messaging.ExchangeNames;
import com.github.provitaliy.messaging.RoutingKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import com.github.provitaliy.messaging.QueueNames;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RabbitConfig {
    private final QueueProperties queueProperties;

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Queue answerMessageQueue() {
        return new Queue(QueueNames.ANSWER_MESSAGE_QUEUE);
    }

    @Bean
    public DirectExchange mainExchange() {
        return new DirectExchange(ExchangeNames.MAIN);
    }

    @Bean
    public Binding answerMessageBinding() {
        return BindingBuilder
                .bind(answerMessageQueue())
                .to(mainExchange())
                .with(RoutingKeys.ROUTING_KEY_ANSWER_MESSAGE);
    }
}
