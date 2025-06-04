package com.github.provitaliy.service.impl;

import com.github.provitaliy.dao.AppDocumentDAO;
import com.github.provitaliy.dao.BinaryContentDAO;
import com.github.provitaliy.entity.AppDocument;
import com.github.provitaliy.entity.BinaryContent;
import com.github.provitaliy.service.FileService;
import com.github.provitaliy.service.TelegramService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.message.Message;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    private final AppDocumentDAO appDocumentDAO;
    private final BinaryContentDAO binaryContentDAO;
    private final TelegramService telegramService;

    @Override
    public AppDocument processDoc(Message externalMessage) {
        var tgDoc = externalMessage.getDocument();
        byte[] fileInBytes = telegramService.downloadFileById(tgDoc.getFileId());
        var binaryContent = binaryContentDAO.save(new BinaryContent(fileInBytes));
        var appDoc = AppDocument.builder()
                .telegramField(tgDoc.getFileId())
                .docName(tgDoc.getFileName())
                .binaryContent(binaryContent)
                .mimeType(tgDoc.getMimeType())
                .fileSize(tgDoc.getFileSize())
                .build();

        return appDocumentDAO.save(appDoc);
    }
}
