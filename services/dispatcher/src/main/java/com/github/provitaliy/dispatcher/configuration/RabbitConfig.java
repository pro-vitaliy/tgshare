package com.github.provitaliy.dispatcher.configuration;

import com.github.provitaliy.common.messaging.ExchangeNames;
import com.github.provitaliy.common.messaging.QueueNames;
import com.github.provitaliy.common.messaging.RoutingKeys;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    public MessageConverter messageConverter() {
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
