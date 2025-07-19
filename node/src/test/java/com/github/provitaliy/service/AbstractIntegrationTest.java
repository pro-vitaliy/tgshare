package com.github.provitaliy.service;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.utility.DockerImageName;

public class AbstractIntegrationTest {

    static RabbitMQContainer rabbitMQContainer = new RabbitMQContainer(
            DockerImageName.parse("rabbitmq:4.1.2-alpine")
                    .asCompatibleSubstituteFor("rabbitmq")
    );

    static {
        rabbitMQContainer.start();
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.rabbitmq.host", rabbitMQContainer::getHost);
        registry.add("spring.rabbitmq.port", rabbitMQContainer::getAmqpPort);
        registry.add("spring.rabbitmq.username", rabbitMQContainer::getAdminUsername);
        registry.add("spring.rabbitmq.password", rabbitMQContainer::getAdminPassword);
    }
}
