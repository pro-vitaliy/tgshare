package com.github.provitaliy.fileservice.service.event;

import com.github.provitaliy.common.event.FileUploadEvent;
import com.github.provitaliy.fileservice.service.file.FileProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ConsumerService {
    private final FileProcessingService processingService;

    public void consumeFileUploadRequest(FileUploadEvent uploadEvent) {
        processingService.handleFileUpload(uploadEvent);
    }
}
