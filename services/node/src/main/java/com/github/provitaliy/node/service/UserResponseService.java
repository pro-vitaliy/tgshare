package com.github.provitaliy.node.service;

import com.github.provitaliy.common.dto.telegram.SendMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class UserResponseService {
    private final ProducerService producerService;

    public void sendUserResponse(Long chatId, String responseText) {
        log.info("Producing user response for chatId {}: {}", chatId, responseText);
        SendMessageDto sendMessage = new SendMessageDto(chatId, responseText);
        producerService.produceAnswer(sendMessage);
    }
}
