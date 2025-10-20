package com.github.provitaliy.service;

import com.github.provitaliy.dto.MailParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailSenderService {
    private final JavaMailSender mailSender;

    @Value("${service.activation.uri}")
    private String serviceActivationUri;

    @Value("${spring.mail.username}")
    private String mailFrom;

    public void send(MailParams mailParams) {
        String id = mailParams.getId();
        String mailTo = mailParams.getMailTo();

        if (id == null || mailTo == null) {
            log.warn("MailParams has null fields: {}", mailParams);
            return;
        }

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(mailFrom);
        mailMessage.setTo(mailTo);
        mailMessage.setSubject("Активация учетной записи");
        mailMessage.setText(getActivationMessageBody(id));

        try {
            log.info("""
                    Отправка письма:
                    → To: {}
                    → From: {}
                    → Subject: {}
                    """, mailTo, mailFrom, mailMessage.getSubject());

            mailSender.send(mailMessage);

        } catch (MailSendException e) {
            log.error("""
                    Не удалось отправить письмо:
                    → To: {}
                    → From: {}
                    → Причина: {}
                    """, mailTo, mailFrom, e.getMessage());
        }
    }

    private String getActivationMessageBody(String id) {
        var message = "Для завершения регистрации пройдите по ссылке:\n" + serviceActivationUri;
        return message.replace("{id}", id);
    }
}
