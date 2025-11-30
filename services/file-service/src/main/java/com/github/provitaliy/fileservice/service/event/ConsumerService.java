package com.github.provitaliy.fileservice.service.event;

import com.github.provitaliy.common.event.FileUploadEvent;
import com.github.provitaliy.common.messaging.QueueNames;
import com.github.provitaliy.fileservice.service.file.FileProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ConsumerService {
    private final FileProcessingService processingService;

    @RabbitListener(queues = QueueNames.FILE_UPLOAD_REQUEST_QUEUE)
    public void consumeFileUploadRequest(FileUploadEvent uploadEvent) {
        processingService.handleFileUpload(uploadEvent);
    }
}
