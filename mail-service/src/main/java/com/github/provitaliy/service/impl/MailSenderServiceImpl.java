package com.github.provitaliy.service.impl;

import com.github.provitaliy.dto.MailParams;
import com.github.provitaliy.service.MailSenderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailSenderServiceImpl implements MailSenderService {
    private final JavaMailSender mailSender;

    @Value("${service.activation.uri}")
    private String serviceActivationUri;

    @Override
    public void send(MailParams mailParams) {
        String id = mailParams.getId();
        String mailTo = mailParams.getMailTo();

        if (id == null || mailTo == null) {
            log.warn("MailParams has null fields: {}", mailParams);
            return;
        }

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(mailTo);
        mailMessage.setSubject("Активация учетной записи");
        mailMessage.setText(getActivationMessageBody(id));

        mailSender.send(mailMessage);
    }

    private String getActivationMessageBody(String id) {
        var message = "Для завершения регистрации пройдите по ссылке:\n" + serviceActivationUri;
        return message.replace("{id}", id);
    }
}
