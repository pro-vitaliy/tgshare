package com.github.provitaliy.fileservice.service;

import com.github.provitaliy.common.event.FileReadyEvent;
import com.github.provitaliy.common.event.FileUploadFailedEvent;
import com.github.provitaliy.fileservice.config.FileServiceProperties;
import com.github.provitaliy.fileservice.config.MinioProperties;
import com.github.provitaliy.fileservice.service.event.ProducerService;
import com.github.provitaliy.fileservice.service.file.FileMetadataService;
import com.github.provitaliy.fileservice.service.file.FileProcessingService;
import com.github.provitaliy.fileservice.service.file.MinioStorageService;
import com.github.provitaliy.fileservice.service.file.TelegramDownloadFileService;
import com.github.provitaliy.fileservice.util.FileLinkGenerator;
import com.github.provitaliy.fileservice.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileProcessingServiceTest {

    private FileProcessingService fileProcessingService;
    private MinioProperties minioProps;
    private FileServiceProperties serviceProps;

    @Mock
    private FileMetadataService metadataService;

    @Mock
    private MinioStorageService minioService;

    @Mock
    private TelegramDownloadFileService downloadFileService;

    @Mock
    private ProducerService producerService;

    @Captor
    private ArgumentCaptor<FileReadyEvent> fileReadyEventCaptor;

    @BeforeEach
    void setUp() {
        minioProps = TestUtils.generateMinioProperties();
        serviceProps = TestUtils.generateFileServiceProperties();
        var linkGenerator = new FileLinkGenerator(serviceProps);
        fileProcessingService = new FileProcessingService(
                metadataService,
                minioService,
                downloadFileService,
                minioProps,
                linkGenerator,
                producerService
        );
    }

    @Test
    void shouldSaveFileAndProduceReadyEvent() throws Exception {
        // given
        var uploadEvent = TestUtils.generateFileUploadEvent();
        var is = new ByteArrayInputStream("test".getBytes());
        var fileMetadata = TestUtils.generateFileMetadata();
        var objectName = "objectName";
        when(downloadFileService.downloadFileById(uploadEvent.getTelegramFileId()))
                .thenReturn(is);

        when(minioService.uploadFile(
                any(InputStream.class),
                eq(uploadEvent.getFileSize()),
                eq(uploadEvent.getFileName()),
                eq(uploadEvent.getMimeType())
        )).thenReturn(objectName);

        when(metadataService.saveMetadata(
                anyLong(),
                anyString(),
                anyString(),
                anyLong(),
                anyString(),
                anyString(),
                any()
        )).thenReturn(fileMetadata);

        // when
        fileProcessingService.handleFileUpload(uploadEvent);

        // then
        verify(downloadFileService).downloadFileById(uploadEvent.getTelegramFileId());
        verify(minioService).uploadFile(
                any(InputStream.class),
                eq(uploadEvent.getFileSize()),
                eq(uploadEvent.getFileName()),
                eq(uploadEvent.getMimeType())
        );

        verify(metadataService).saveMetadata(
                eq(uploadEvent.getTelegramUserId()),
                eq(uploadEvent.getFileName()),
                eq(uploadEvent.getMimeType()),
                eq(uploadEvent.getFileSize()),
                eq(minioProps.getBucket()),
                eq(objectName),
                any()
        );

        verify(producerService).produceFileReadyEvent(fileReadyEventCaptor.capture());
        var capturedEvent = fileReadyEventCaptor.getValue();
        var expectedUrl = String.format("%s%s%s",
                serviceProps.getUrl(),
                serviceProps.getDownloadEndpoint(),
                fileMetadata.getUuid());

        assertEquals(uploadEvent.getTelegramUserId(), capturedEvent.telegramUserId());
        assertEquals(expectedUrl, capturedEvent.fileUrl());
    }

    @Test
    void shouldRollbackOnFailure() throws Exception {
        // given
        var uploadEvent = TestUtils.generateFileUploadEvent();
        var is = new ByteArrayInputStream("test".getBytes());
        var objectName = "objectName";
        when(downloadFileService.downloadFileById(uploadEvent.getTelegramFileId()))
                .thenReturn(is);
        when(minioService.uploadFile(
                any(InputStream.class),
                eq(uploadEvent.getFileSize()),
                eq(uploadEvent.getFileName()),
                eq(uploadEvent.getMimeType())
        )).thenReturn(objectName);

        when(metadataService.saveMetadata(
                anyLong(),
                anyString(),
                anyString(),
                anyLong(),
                anyString(),
                anyString(),
                any()
        )).thenThrow(new RuntimeException("DB error"));

        // when
        fileProcessingService.handleFileUpload(uploadEvent);

        // then
        verify(minioService).deleteFile(eq(objectName));
        verify(metadataService).deleteMetadata(eq(objectName));

        ArgumentCaptor<FileUploadFailedEvent> failedEventCaptor = ArgumentCaptor.forClass(FileUploadFailedEvent.class);
        verify(producerService).produceFileUploadFailedEvent(failedEventCaptor.capture());
        var failedEvent = failedEventCaptor.getValue();
        assertEquals(uploadEvent.getTelegramUserId(), failedEvent.telegramUserId());
        assertEquals(uploadEvent.getFileName(), failedEvent.fileName());
        }
}
