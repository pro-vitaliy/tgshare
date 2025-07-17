package com.github.provitaliy.configuration;

import com.github.provitaliy.messaging.ExchangeNames;
import com.github.provitaliy.messaging.QueueNames;
import com.github.provitaliy.messaging.RoutingKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class RabbitConfig {

    @Bean
    public MessageConverter jsonMessageConverter() {
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
    public Binding textMessageBinding() {
        return BindingBuilder
                .bind(textMessageQueue())
                .to(mainExchange())
                .with(RoutingKeys.ROUTING_KEY_TEXT_MESSAGE_UPDATE);
    }

    @Bean
    public Binding docMessageBinding() {
        return BindingBuilder
                .bind(docMessageQueue())
                .to(mainExchange())
                .with(RoutingKeys.ROUTING_KEY_DOC_MESSAGE_UPDATE);
    }

    @Bean
    public Binding photoMessageBinding() {
        return BindingBuilder
                .bind(photoMessageQueue())
                .to(mainExchange())
                .with(RoutingKeys.ROUTING_KEY_PHOTO_MESSAGE_UPDATE);
    }
}
