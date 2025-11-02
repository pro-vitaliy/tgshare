package com.github.provitaliy.node.handler;

import com.github.provitaliy.common.event.FileUploadEvent;
import com.github.provitaliy.node.bot.BotResponse;
import com.github.provitaliy.node.service.NodeUserService;
import com.github.provitaliy.node.service.ProducerService;
import com.github.provitaliy.node.user.NodeUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.photo.PhotoSize;

@Slf4j
@RequiredArgsConstructor
@Service
public class FileUpdateHandler {
    private final NodeUserService userService;
    private final ProducerService producerService;

    public void handleDocUpdate(Update update) {
        Message message = update.getMessage();

        if (message == null || message.getDocument() == null) {
            log.warn("Received update without document: {}", update);
            return;
        }

        NodeUser user = userService.getOrCreateAppUser(HandlerUtils.buildUserCreateDto(message));
        if (!user.getIsActive()) {
            processForbiddenAnswer(user);
            return;
        }

        Document telegramDocument = message.getDocument();
        FileUploadEvent fileUploadEvent = FileUploadEvent.builder()
                .telegramFileId(telegramDocument.getFileId())
                .fileName(telegramDocument.getFileName())
                .mimeType(telegramDocument.getMimeType())
                .fileSize(telegramDocument.getFileSize())
                .ownerId(user.getId())
                .build();

        processUpload(fileUploadEvent, user);
    }

    public void handlePhotoUpdate(Update update) {
        Message message = update.getMessage();

        if (message == null || message.getPhoto() == null || message.getPhoto().isEmpty()) {
            log.warn("Received update without photo: {}", update);
            return;
        }

        NodeUser user = userService.getOrCreateAppUser(HandlerUtils.buildUserCreateDto(message));
        if (!user.getIsActive()) {
            processForbiddenAnswer(user);
            return;
        }

        PhotoSize telegramPhoto = message.getPhoto().getLast();
        FileUploadEvent fileUploadEvent = FileUploadEvent.builder()
                .telegramFileId(telegramPhoto.getFileId())
                .fileName("photo_" + telegramPhoto.getFileId() + ".jpg")
                .fileSize(Long.valueOf(telegramPhoto.getFileSize()))
                .ownerId(user.getId())
                .build();

        processUpload(fileUploadEvent, user);
    }

    private void processUpload(FileUploadEvent event, NodeUser user) {
        producerService.produceFileUploadRequest(event);
        SendMessage answerMessage = HandlerUtils.prepareMessage(BotResponse.FILE_RECEIVED_RESPONSE, user.getChatId());
        producerService.produceAnswer(answerMessage);
    }

    private void processForbiddenAnswer(NodeUser user) {
        SendMessage forbiddenAnswer = HandlerUtils.prepareMessage(BotResponse.NOT_ALLOW_TO_SEND_FILE_RESPONSE,
                user.getChatId());
        producerService.produceAnswer(forbiddenAnswer);
    }
}
