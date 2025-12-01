package com.github.provitaliy.node.handler;

import com.github.provitaliy.common.event.EmailAlreadyTakenEvent;
import com.github.provitaliy.common.event.FileReadyEvent;
import com.github.provitaliy.common.event.FileUploadFailedEvent;
import com.github.provitaliy.common.event.UserActivatedEvent;
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

        String messageText = "Ваш email успешно подтвержден, учетная запись активирована!";
        userResponseService.sendUserResponse(nodeUser.getChatId(), messageText);
    }

    public void handle(FileReadyEvent fileReadyEvent) {
        String messageText = "Файл успешно, загружен и доступен по ссылке: %s".formatted(fileReadyEvent.fileUrl());
        NodeUser user = userService.getByTelegramUserId(fileReadyEvent.telegramUserId());
        userResponseService.sendUserResponse(user.getChatId(), messageText);
    }

    public void handle(EmailAlreadyTakenEvent event) {
        NodeUser user = userService.getByTelegramUserId(event.telegramUserId());
        String messageText = "Email %s уже занят. Пожалуйста, используйте другой email.".formatted(event.email());
        userResponseService.sendUserResponse(user.getChatId(), messageText);
    }

    public void handle(FileUploadFailedEvent event) {
        NodeUser user = userService.getByTelegramUserId(event.telegramUserId());
        String messageText = "Не удалось загрузить файл %s. Пожалуйста, попробуйте еще раз.".formatted(event.fileName());
        userResponseService.sendUserResponse(user.getChatId(), messageText);
    }
}
