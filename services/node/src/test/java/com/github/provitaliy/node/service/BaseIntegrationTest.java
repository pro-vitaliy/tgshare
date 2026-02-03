package com.github.provitaliy.node.service;

import com.github.provitaliy.node.stub.FakeAppUserGrpcService;
import com.github.provitaliy.node.testConfig.TestGrpcConfig;
import com.github.provitaliy.node.testConfig.TestRabbitConfig;
import org.junit.jupiter.api.AfterEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

@ActiveProfiles("test")
@Import({TestGrpcConfig.class, TestRabbitConfig.class})
@Testcontainers
@SpringBootTest
public abstract class BaseIntegrationTest {

    @AfterEach
    public void clearGrpcFake() {
        FakeAppUserGrpcService.clearUserStatus();
    }

    /*
     * Контейнеры Testcontainers (RabbitMQ, Redis) не создаются здесь,
     * чтобы сохранить параллельный запуск тестов через Maven.
     * Если их вынести в базовый класс, тесты будут делить одни и те же контейнеры,
     * что приведёт к проблемам при параллельном выполнении.
     */

}