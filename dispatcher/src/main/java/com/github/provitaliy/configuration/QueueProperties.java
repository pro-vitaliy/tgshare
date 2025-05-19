package com.github.provitaliy.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "spring.rabbitmq.queues")
public class QueueProperties {
    private String textMessageUpdate;
    private String docMessageUpdate;
    private String photoMessageUpdate;
    private String answerMessage;
}
