package com.github.provitaliy.dispatcher.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Slf4j
@RequiredArgsConstructor
@Service
public class TelegramWebhookService {

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.bot.url}")
    private String botUrl;

    private final RestClient restClient;

    public void setWebhook() {
        String url = "https://api.telegram.org/bot{token}/setWebhook?url={webhook}";
        log.info("Установка Telegram вебхука на адрес: {}", botUrl);

        try {
            restClient.get()
                    .uri(url, botToken, botUrl)
                    .retrieve()
                    .toBodilessEntity();

            log.info("Вебхук Telegram успешно установлен.");

        } catch (RestClientException e) {
            throw new IllegalStateException("Ошибка при установке вебхука: " + e.getMessage(), e);
        }
    }
}
