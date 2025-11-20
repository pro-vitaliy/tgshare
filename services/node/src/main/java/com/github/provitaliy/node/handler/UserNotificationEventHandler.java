package com.github.provitaliy.node.handler;

import com.github.provitaliy.common.dto.telegram.SendMessageDto;
import com.github.provitaliy.node.service.NodeUserService;
import com.github.provitaliy.node.service.ProducerService;
import com.github.provitaliy.node.user.NodeUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserNotificationEventHandler {
    private final ProducerService producer;
    private final NodeUserService nodeUserService;

    public void handleFileReadyEvent(Long telegramUserId, String downloadLink) {
        String messageText = "Файл успешно, загружен и доступен по ссылке: %s".formatted(downloadLink);
        NodeUser user = nodeUserService.getByTelegramUserId(telegramUserId);
        SendMessageDto message = HandlerUtils.prepareSendMessage(messageText, user.getChatId());
        producer.produceAnswer(message);
    }
}
