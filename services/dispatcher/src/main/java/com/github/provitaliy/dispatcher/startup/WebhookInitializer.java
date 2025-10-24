package com.github.provitaliy.dispatcher.startup;

import com.github.provitaliy.dispatcher.service.TelegramWebhookService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class WebhookInitializer implements ApplicationRunner {

    private final TelegramWebhookService webhookService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        webhookService.setWebhook();
    }
}
