package com.github.provitaliy.fileservice.service.file;

import com.github.provitaliy.common.event.FileReadyEvent;
import com.github.provitaliy.common.event.FileUploadEvent;
import com.github.provitaliy.fileservice.config.MinioProperties;
import com.github.provitaliy.fileservice.entity.FileMetadata;
import com.github.provitaliy.fileservice.service.event.ProducerService;
import com.github.provitaliy.fileservice.util.FileLinkGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

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

//        TODO: заменить byte[] на потоковую передачу файлов из телеграмма в минио

        byte[] file = downloadFileService.downloadFileById(event.getTelegramFileId());
        String objectName = minioService.uploadFile(file, event.getFileName(), event.getMimeType());
        FileMetadata metadata = metadataService.saveMetadata(
                event.getTelegramUserId(),
                event.getFileName(),
                event.getMimeType(),
                event.getFileSize(),
                minioProperties.getBucket(),
                objectName,
                Duration.ofDays(minioProperties.getDefaultTtlDays())
        );

        String downloadLink = linkGenerator.generateLink(metadata.getUuid());
        FileReadyEvent readyEvent = new FileReadyEvent(event.getTelegramUserId(), downloadLink);
        producerService.produceFileReadyEvent(readyEvent);
    }
}
