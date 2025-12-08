package com.github.provitaliy.node.handler;

import com.github.provitaliy.common.event.EmailAlreadyTakenEvent;
import com.github.provitaliy.common.event.FileReadyEvent;
import com.github.provitaliy.common.event.FileUploadFailedEvent;
import com.github.provitaliy.common.event.UserActivatedEvent;
import com.github.provitaliy.node.bot.BotResponse;
import com.github.provitaliy.node.service.NodeUserCacheService;
import com.github.provitaliy.node.service.NodeUserService;
import com.github.provitaliy.node.service.UserResponseService;
import com.github.provitaliy.node.user.NodeUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserDomainEventHandler {
    private final NodeUserService userService;
    private final NodeUserCacheService cacheService;
    private final UserResponseService userResponseService;

    public void handle(UserActivatedEvent activatedEvent) {
        NodeUser nodeUser = userService.getByTelegramUserId(activatedEvent.telegramUserId());
        nodeUser.setIsActive(true);
        nodeUser.setEmail(activatedEvent.email());
        cacheService.save(nodeUser);

        userResponseService.sendUserResponse(nodeUser.getChatId(), BotResponse.USER_ACTIVATED_RESPONSE);
    }

    public void handle(FileReadyEvent fileReadyEvent) {
        String messageText = BotResponse.FILE_READY_RESPONSE.formatted(fileReadyEvent.fileUrl());
        NodeUser user = userService.getByTelegramUserId(fileReadyEvent.telegramUserId());
        userResponseService.sendUserResponse(user.getChatId(), messageText);
    }

    public void handle(EmailAlreadyTakenEvent event) {
        NodeUser user = userService.getByTelegramUserId(event.telegramUserId());
        String messageText = BotResponse.EMAIL_ALREADY_EXIST.formatted(event.email());
        userResponseService.sendUserResponse(user.getChatId(), messageText);
    }

    public void handle(FileUploadFailedEvent event) {
        NodeUser user = userService.getByTelegramUserId(event.telegramUserId());
        String messageText = BotResponse.FILE_UPLOAD_FAILURE_RESPONSE.formatted(event.fileName());
        userResponseService.sendUserResponse(user.getChatId(), messageText);
    }
}
