package com.github.provitaliy.node.handler;

import com.github.provitaliy.common.dto.telegram.SendMessageDto;
import com.github.provitaliy.common.dto.telegram.TelegramDocumentMessageDto;
import com.github.provitaliy.common.dto.telegram.TelegramPhotoMessageDto;
import com.github.provitaliy.common.event.FileUploadEvent;
import com.github.provitaliy.node.bot.BotResponse;
import com.github.provitaliy.node.service.NodeUserService;
import com.github.provitaliy.node.service.ProducerService;
import com.github.provitaliy.node.user.NodeUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class FileUpdateHandler {
    private final NodeUserService userService;
    private final ProducerService producerService;

    public void handleDocUpdate(TelegramDocumentMessageDto docMessage) {
        if (docMessage == null || docMessage.documentId() == null) {
            log.warn("Received message without document: {}", docMessage);
            return;
        }

        NodeUser user = userService.getOrCreateAppUser(HandlerUtils.buildUserCreateDto(docMessage));
        if (!user.getIsActive()) {
            processForbiddenAnswer(user);
            return;
        }

        FileUploadEvent fileUploadEvent = FileUploadEvent.builder()
                .telegramFileId(docMessage.documentId())
                .fileName(docMessage.documentName())
                .mimeType(docMessage.mimeType())
                .fileSize(docMessage.documentSize())
                .telegramUserId(user.getTelegramUserId())
                .build();

        processUpload(fileUploadEvent, user);
    }

    public void handlePhotoUpdate(TelegramPhotoMessageDto photoMessage) {
        if (photoMessage == null || photoMessage.photoId() == null || photoMessage.photoId().isEmpty()) {
            log.warn("Received message without photo: {}", photoMessage);
            return;
        }

        NodeUser user = userService.getOrCreateAppUser(HandlerUtils.buildUserCreateDto(photoMessage));
        if (!user.getIsActive()) {
            processForbiddenAnswer(user);
            return;
        }

        FileUploadEvent fileUploadEvent = FileUploadEvent.builder()
                .telegramFileId(photoMessage.photoId())
                .fileName("photo_" + photoMessage.photoId() + ".jpg")
                .fileSize(photoMessage.photoSize())
                .telegramUserId(user.getTelegramUserId())
                .build();

        processUpload(fileUploadEvent, user);
    }

    private void processUpload(FileUploadEvent event, NodeUser user) {
        producerService.produceFileUploadRequest(event);
        SendMessageDto answerMessage = HandlerUtils.prepareSendMessage(BotResponse.FILE_RECEIVED_RESPONSE, user.getChatId());
        producerService.produceAnswer(answerMessage);
    }

    private void processForbiddenAnswer(NodeUser user) {
        SendMessageDto forbiddenAnswer = HandlerUtils.prepareSendMessage(BotResponse.NOT_ALLOW_TO_SEND_FILE_RESPONSE,
                user.getChatId());
        producerService.produceAnswer(forbiddenAnswer);
    }
}
