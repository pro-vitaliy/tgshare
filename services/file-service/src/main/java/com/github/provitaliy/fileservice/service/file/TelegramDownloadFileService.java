package com.github.provitaliy.fileservice.service.file;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.provitaliy.fileservice.config.TelegramProperties;
import com.github.provitaliy.fileservice.exception.DownloadFileException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@RequiredArgsConstructor
@Service
public class TelegramDownloadFileService {
    private final CloseableHttpClient httpClient;
    private final RestClient restClient;
    private final TelegramProperties props;

    @Retryable(
            retryFor = {RestClientException.class, IOException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    public InputStream downloadFileById(String telegramFileId) throws IOException {
        String filePath = fetchFilePath(telegramFileId);
        String storageUrl = props.getFileStorageUrl()
                .replace("{token}", props.getBotToken())
                .replace("{filePath}", filePath);

        CloseableHttpResponse response = httpClient.execute(new HttpGet(storageUrl));

        if (response.getStatusLine().getStatusCode() != 200) {
            log.error("Failed to download file from Telegram, status: {}", response.getStatusLine());
            throw new DownloadFileException("Failed to download file from Telegram");
        }
        return response.getEntity().getContent();
    }

    @Recover
    public InputStream recover(Exception e, String fileId) {
        log.error("Failed to download Telegram file after retries, fileId={}", fileId, e);
        throw new DownloadFileException("Failed to download file from Telegram after retries", e);
    }

    private String fetchFilePath(String fileId) {
        String infoUrl = props.getFileInfoUrl()
                .replace("{token}", props.getBotToken())
                .replace("{fileId}", fileId);

        JsonNode fileInfo = restClient.get()
                .uri(infoUrl)
                .retrieve()
                .body(JsonNode.class);

        if (fileInfo == null || !fileInfo.path("result").has("file_path")) {
            log.error("Telegram response invalid: {}", fileInfo);
            throw new DownloadFileException("Telegram response invalid: file path missing");
        }

        return fileInfo.path("result")
                .path("file_path")
                .asText();
    }
}
