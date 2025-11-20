package com.github.provitaliy.node.handler;

import com.github.provitaliy.common.dto.telegram.SendMessageDto;
import com.github.provitaliy.common.event.UserActivatedEvent;
import com.github.provitaliy.node.service.NodeUserService;
import com.github.provitaliy.node.service.ProducerService;
import com.github.provitaliy.node.user.NodeUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class NodeUserEventHandler {
    private final NodeUserService userService;
    private final ProducerService producerService;

    public void userActivatedEventHandler(UserActivatedEvent activatedEvent) {
        NodeUser nodeUser = userService.getByTelegramUserId(activatedEvent.telegramUserId());
        nodeUser.setIsActive(true);
        nodeUser.setEmail(activatedEvent.email());
        userService.updateNodeUser(nodeUser);

        String messageText = "Ваш email успешно подтвержден, учетная запись активирована!";
        SendMessageDto message = HandlerUtils.prepareSendMessage(messageText, nodeUser.getChatId());
        producerService.produceAnswer(message);
    }
}
