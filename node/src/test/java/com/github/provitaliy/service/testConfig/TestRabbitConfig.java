package com.github.provitaliy.service.testConfig;

import com.github.provitaliy.messaging.ExchangeNames;
import com.github.provitaliy.messaging.QueueNames;
import com.github.provitaliy.messaging.RoutingKeys;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestRabbitConfig {

    @Bean
    public Queue answerMessageQueue() {
        return new Queue(QueueNames.ANSWER_MESSAGE_QUEUE);
    }

    @Bean
    public Queue registrationMailQueue() {
        return new Queue(QueueNames.REGISTRATION_MAIL_QUEUE);
    }

    @Bean
    public Binding answerMessageBinding() {
        return BindingBuilder
                .bind(answerMessageQueue())
                .to(new DirectExchange(ExchangeNames.MAIN))
                .with(RoutingKeys.ROUTING_KEY_ANSWER_MESSAGE);
    }

    @Bean
    public Binding registrationMailBinding() {
        return BindingBuilder
                .bind(registrationMailQueue())
                .to(new DirectExchange(ExchangeNames.MAIN))
                .with(RoutingKeys.ROUTING_KEY_EMAIL_MESSAGE);
    }
}
