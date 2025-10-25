package com.github.provitaliy.node.configuration;

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
    public Queue textMessageQueue() {
        return new Queue(QueueNames.TEXT_MESSAGE_UPDATE_QUEUE);
    }

    @Bean
    public Queue docMessageQueue() {
        return new Queue(QueueNames.DOC_MESSAGE_UPDATE_QUEUE);
    }

    @Bean
    public Queue photoMessageQueue() {
        return new Queue(QueueNames.PHOTO_MESSAGE_UPDATE_QUEUE);
    }

    @Bean
    public DirectExchange mainExchange() {
        return new DirectExchange(ExchangeNames.MAIN);
    }

    @Bean
    public Binding textMessageBinding(Queue textMessageQueue, DirectExchange mainExchange) {
        return BindingBuilder
                .bind(textMessageQueue)
                .to(mainExchange)
                .with(RoutingKeys.ROUTING_KEY_TEXT_MESSAGE_UPDATE);
    }

    @Bean
    public Binding docMessageBinding(Queue docMessageQueue, DirectExchange mainExchange) {
        return BindingBuilder
                .bind(docMessageQueue)
                .to(mainExchange)
                .with(RoutingKeys.ROUTING_KEY_DOC_MESSAGE_UPDATE);
    }

    @Bean
    public Binding photoMessageBinding(Queue photoMessageQueue, DirectExchange mainExchange) {
        return BindingBuilder
                .bind(photoMessageQueue)
                .to(mainExchange)
                .with(RoutingKeys.ROUTING_KEY_PHOTO_MESSAGE_UPDATE);
    }
}
