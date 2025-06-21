package com.github.provitaliy.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.provitaliy.exception.UploadFileException;
import com.github.provitaliy.service.TelegramService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Slf4j
@Service
public class TelegramServiceImpl implements TelegramService {
    private final String token;
    private final String fileInfoUri;
    private final String fileStorageUri;
    private final RestClient restClient;

    public TelegramServiceImpl(
            @Value("${telegram.bot.token}") String token,
            @Value("${telegram.endpoints.file-info}") String fileInfoUri,
            @Value("${telegram.endpoints.file-storage}") String fileStorageUri,
            RestClient restClient
    ) {
        this.token = token;
        this.fileInfoUri = fileInfoUri;
        this.fileStorageUri = fileStorageUri;
        this.restClient = restClient;
    }

    @Override
    public byte[] downloadFileById(String fileId) {
        String filePath = fetchFilePath(fileId);
        return restClient.get()
                .uri(fileStorageUri, token, filePath)
                .retrieve()
                .body(byte[].class);
    }

    private String fetchFilePath(String fileId) {
        JsonNode fileInfo = restClient.get()
                .uri(fileInfoUri, token, fileId)
                .retrieve()
                .body(JsonNode.class);

        if (fileInfo == null || !fileInfo.path("result").has("file_path")) {
            log.error("Invalid Telegram file info response: {}", fileInfo);
            throw new UploadFileException("Failed to load file. File path not found.");
        }

        return fileInfo.path("result")
                .path("file_path")
                .asText();
    }
}
