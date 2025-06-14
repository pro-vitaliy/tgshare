package com.github.provitaliy.service;

import com.github.provitaliy.dto.MailParams;

public interface ConsumerService {
    void consumeRegistrationMail(MailParams mailParams);
}
