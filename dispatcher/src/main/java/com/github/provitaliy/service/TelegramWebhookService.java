package com.github.provitaliy.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramWebhookService {

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.bot.uri}")
    private String botUri;

    private final RestClient restClient;


    public void setWebhook() {
        String url = "https://api.telegram.org/bot{token}/setWebhook?url={webhook}";
        log.info("Установка Telegram вебхука на адрес: {}", botUri);

        try {
            restClient.get()
                    .uri(url, botToken, botUri)
                    .retrieve()
                    .toBodilessEntity();

            log.info("Вебхук Telegram успешно установлен.");

        } catch (RestClientException e) {
            log.error("Ошибка при установке Telegram вебхука: {}", e.getMessage(), e);
            throw new IllegalStateException("Невозможно продолжить. Ошибка при установке вебхука: " + e.getMessage(), e);
        }
    }
}
