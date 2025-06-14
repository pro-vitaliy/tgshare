package com.github.provitaliy.service;

import com.github.provitaliy.dto.MailParams;

public interface MailSenderService {
    void send(MailParams mailParams);
}
