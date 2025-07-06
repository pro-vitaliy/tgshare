package com.github.provitaliy.startup;

import com.github.provitaliy.service.TelegramWebhookService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WebhookInitializer implements ApplicationRunner {

    private final TelegramWebhookService webhookService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        webhookService.setWebhook();
    }
}
