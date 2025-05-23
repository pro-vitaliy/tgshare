package com.github.provitaliy.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Queue;
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
    public Queue textMessageQueue() {
        return new Queue(queueProperties.getTextMessageUpdate());
    }

    @Bean
    public Queue docMessageQueue() {
        return new Queue(queueProperties.getDocMessageUpdate());
    }

    @Bean
    public Queue photoMessageQueue() {
        return new Queue(queueProperties.getPhotoMessageUpdate());
    }

    @Bean
    public Queue answerMessageQueue() {
        return new Queue(queueProperties.getAnswerMessage());
    }
}
