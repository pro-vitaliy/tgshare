package com.github.provitaliy.notificationservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class MailSenderService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String mailFrom;

    @Retryable(
            retryFor = MailException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    public void send(String mailTo, String subject, String body) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(mailFrom);
        mailMessage.setTo(mailTo);
        mailMessage.setSubject(subject);
        mailMessage.setText(body);
        log.debug("Sending mail to → {}, subject → {}", mailTo, mailMessage.getSubject());
        mailSender.send(mailMessage);
        log.debug("Mail sent to → {}", mailTo);
    }

    @Recover
    public void recover(MailException e, String mailTo, String subject, String body) {
        log.error("""
        Не удалось отправить письмо:
        → To: {}
        → Reason: {}
        """, mailTo, e.getMessage());
    }
}
