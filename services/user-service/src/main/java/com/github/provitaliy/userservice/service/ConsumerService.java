package com.github.provitaliy.userservice.service;

import com.github.provitaliy.common.event.UserEmailEnteredEvent;
import com.github.provitaliy.common.messaging.QueueNames;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ConsumerService {
    private final AppUserService appUserService;

    @RabbitListener(queues = QueueNames.USER_EMAIL_ENTERED_QUEUE)
    public void consumeUserEmailEnteredEvent(UserEmailEnteredEvent emailEnteredEvent) {
        appUserService.updateUnconfirmedEmail(emailEnteredEvent.getUserId(), emailEnteredEvent.getEmail());
    }
}
