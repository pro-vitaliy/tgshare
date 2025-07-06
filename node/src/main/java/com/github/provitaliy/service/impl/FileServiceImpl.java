package com.github.provitaliy.service.impl;

import com.github.provitaliy.dao.AppDocumentDAO;
import com.github.provitaliy.dao.AppPhotoDAO;
import com.github.provitaliy.dao.BinaryContentDAO;
import com.github.provitaliy.entity.AppDocument;
import com.github.provitaliy.entity.AppPhoto;
import com.github.provitaliy.entity.BinaryContent;
import com.github.provitaliy.service.FileService;
import com.github.provitaliy.service.TelegramService;
import com.github.provitaliy.service.enums.LinkType;
import lombok.RequiredArgsConstructor;
import org.hashids.Hashids;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.message.Message;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    private final AppDocumentDAO appDocumentDAO;
    private final AppPhotoDAO appPhotoDAO;
    private final BinaryContentDAO binaryContentDAO;
    private final TelegramService telegramService;
    private final Hashids hashids;

    @Value("${files.link}")
    private String linkAddress;

    @Override
    public AppDocument processDoc(Message telegramMessage) {
        var tgDoc = telegramMessage.getDocument();
        byte[] fileInBytes = telegramService.downloadFileById(tgDoc.getFileId());
        var binaryContent = binaryContentDAO.save(new BinaryContent(fileInBytes));
        var appDoc = AppDocument.builder()
                .telegramFileId(tgDoc.getFileId())
                .docName(tgDoc.getFileName())
                .binaryContent(binaryContent)
                .mimeType(tgDoc.getMimeType())
                .fileSize(tgDoc.getFileSize())
                .build();

        return appDocumentDAO.save(appDoc);
    }

    @Override
    public AppPhoto processPhoto(Message telegramMessage) {
        PhotoSize tgPhoto = telegramMessage.getPhoto().getLast();
        byte[] fileInBytes = telegramService.downloadFileById(tgPhoto.getFileId());
        var binaryContent = binaryContentDAO.save(new BinaryContent(fileInBytes));
        var appPhoto = AppPhoto.builder()
                .telegramFileId(tgPhoto.getFileId())
                .binaryContent(binaryContent)
                .fileSize(tgPhoto.getFileSize())
                .build();
        return appPhotoDAO.save(appPhoto);
    }

    @Override
    public String generateLink(Long fileId, LinkType linkType) {
        var hash = hashids.encode(fileId);
        return linkAddress + "/api/" + linkType + "?id=" + hash;
    }
}
