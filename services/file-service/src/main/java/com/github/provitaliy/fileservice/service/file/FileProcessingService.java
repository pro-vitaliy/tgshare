package com.github.provitaliy.fileservice.service.file;

import com.github.provitaliy.common.event.FileReadyEvent;
import com.github.provitaliy.common.event.FileUploadEvent;
import com.github.provitaliy.common.event.FileUploadFailedEvent;
import com.github.provitaliy.fileservice.config.MinioProperties;
import com.github.provitaliy.fileservice.entity.FileMetadata;
import com.github.provitaliy.fileservice.service.event.ProducerService;
import com.github.provitaliy.fileservice.util.FileLinkGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.Duration;

@Slf4j
@RequiredArgsConstructor
@Service
public class FileProcessingService {
    private final FileMetadataService metadataService;
    private final MinioStorageService minioService;
    private final TelegramDownloadFileService downloadFileService;
    private final MinioProperties minioProperties;
    private final FileLinkGenerator linkGenerator;
    private final ProducerService producerService;

    public void handleFileUpload(FileUploadEvent event) {
        String objectName = null;
        try (InputStream is = downloadFileService.downloadFileById(event.getTelegramFileId())) {
            objectName = storeFileAndGetObjectName(is, event);
            FileMetadata metadata = saveFileMetadata(event, objectName);
            String downloadLink = linkGenerator.generateLink(metadata.getUuid());

            FileReadyEvent readyEvent = new FileReadyEvent(event.getTelegramUserId(), downloadLink);
            producerService.produceFileReadyEvent(readyEvent);
        } catch (Exception e) {
            rollback(objectName);
            handleFailure(event, e);
            log.error("Error processing file upload: telegramFileId={}, telegramUserId={}",
                    event.getTelegramFileId(), event.getTelegramUserId(), e);
        }
    }

    private void rollback(String objectName) {
        if (objectName == null) {
            return;
        }

        try {
            minioService.deleteFile(objectName);
        } catch (Exception e) {
            log.error("Rollback: failed to delete file from MinIO, objectName={}", objectName, e);
        }

        try {
            metadataService.deleteMetadata(objectName);
        } catch (Exception e) {
            log.error("Rollback: failed to delete metadata, objectName={}", objectName, e);
        }
    }

    private String storeFileAndGetObjectName(InputStream is, FileUploadEvent event) {
        return minioService.uploadFile(
                is,
                event.getFileSize(),
                event.getFileName(),
                event.getMimeType()
        );
    }

    private FileMetadata saveFileMetadata(FileUploadEvent event, String objectName) {
        return metadataService.saveMetadata(
                event.getTelegramUserId(),
                event.getFileName(),
                event.getMimeType(),
                event.getFileSize(),
                minioProperties.getBucket(),
                objectName,
                Duration.ofDays(minioProperties.getDefaultTtlDays())
        );
    }

    private void handleFailure(FileUploadEvent event, Exception e) {
        var failedEvent = new FileUploadFailedEvent(event.getTelegramUserId(), event.getFileName());
        producerService.produceFileUploadFailedEvent(failedEvent);
    }
}
