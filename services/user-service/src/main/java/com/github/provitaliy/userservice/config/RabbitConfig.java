package com.github.provitaliy.userservice.config;

import com.github.provitaliy.common.messaging.ExchangeNames;
import com.github.provitaliy.common.messaging.QueueNames;
import com.github.provitaliy.common.messaging.RoutingKeys;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Queue userEmailEnteredQueue() {
        return QueueBuilder.durable(QueueNames.USER_EMAIL_ENTERED_QUEUE)
                .withArgument("x-dead-letter-exchange", ExchangeNames.DLQ)
                .withArgument("x-dead-letter-routing-key", RoutingKeys.ROUTING_KEY_USER_EMAIL_ENTERED_DLQ)
                .build();
    }

    @Bean
    public DirectExchange mainExchange() {
        return new DirectExchange(ExchangeNames.MAIN);
    }

    @Bean
    public Binding userEmailEnteredBinding() {
        return BindingBuilder
                .bind(userEmailEnteredQueue())
                .to(mainExchange())
                .with(RoutingKeys.ROUTING_KEY_USER_EMAIL_ENTERED);
    }
}
