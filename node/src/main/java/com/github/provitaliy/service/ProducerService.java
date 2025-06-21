package com.github.provitaliy.service;

import com.github.provitaliy.dto.MailParams;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface ProducerService {
    void produceAnswer(SendMessage sendMessage);
    void produceRegistrationMail(MailParams mailParams);
}
