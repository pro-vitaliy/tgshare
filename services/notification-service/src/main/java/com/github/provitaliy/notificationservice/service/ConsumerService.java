package com.github.provitaliy.notificationservice.service;

import com.github.provitaliy.common.event.SendEmailEvent;
import com.github.provitaliy.common.messaging.QueueNames;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ConsumerService {
    private final MailSenderService mailSenderService;

    @RabbitListener(queues = QueueNames.EMAIL_SEND_QUEUE)
    public void consumeEmailSendQueue(SendEmailEvent sendEmailEvent) {
        mailSenderService.send(
                sendEmailEvent.getMailTo(),
                sendEmailEvent.getSubject(),
                sendEmailEvent.getText()
        );
    }
}
