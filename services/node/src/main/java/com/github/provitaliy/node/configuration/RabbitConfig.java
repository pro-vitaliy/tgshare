package com.github.provitaliy.node.configuration;

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
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

//    --------------- Queues ----------------

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
    public Queue fileReadyQueue() {
        return QueueBuilder.durable(QueueNames.FILE_READY_QUEUE)
                .withArgument("x-dead-letter-exchange", ExchangeNames.DLQ)
                .withArgument("x-dead-letter-routing-key", RoutingKeys.ROUTING_KEY_FILE_READY_DLQ)
                .build();
    }

    @Bean
    public Queue userActivatedQueue() {
        return QueueBuilder.durable(QueueNames.USER_ACTIVATED_QUEUE)
                .withArgument("x-dead-letter-exchange", ExchangeNames.DLQ)
                .withArgument("x-dead-letter-routing-key", RoutingKeys.ROUTING_KEY_USER_ACTIVATED_DLQ)
                .build();
    }

//    --------------- Exchanges ----------------

    @Bean
    public DirectExchange mainExchange() {
        return new DirectExchange(ExchangeNames.MAIN);
    }

//    --------------- Bindings ----------------

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

    @Bean
    public Binding fileReadyBinding() {
        return BindingBuilder
                .bind(fileReadyQueue())
                .to(mainExchange())
                .with(RoutingKeys.ROUTING_KEY_FILE_READY);
    }

    @Bean
    public Binding userActivatedBinding() {
        return BindingBuilder
                .bind(userActivatedQueue())
                .to(mainExchange())
                .with(RoutingKeys.ROUTING_KEY_USER_ACTIVATED);
    }
}
