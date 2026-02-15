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
public class MailSenderService implements NotificationSenderService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String mailFrom;

    @Retryable(
            retryFor = MailException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    @Override
    public void send(String mailTo, String subject, String body) {
        try {
            if (mailTo == null) {
                log.warn("Mail 'to' address is null, skipping sending mail with subject → {}", subject);
                return;
            }
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(mailFrom);
            mailMessage.setTo(mailTo);
            mailMessage.setSubject(subject);
            mailMessage.setText(body);
            log.debug("Sending mail to → {}, subject → {}", mailTo, mailMessage.getSubject());
            mailSender.send(mailMessage);
            log.info("Mail sent successfully to → {}, subject → {}", mailTo, subject);
        } catch (MailException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while sending mail to → {}: {}", mailTo, e.getMessage());
        }
    }

    @Recover
    public void recover(MailException e, String mailTo, String subject, String body) {
        log.error("""
        Failed to send mail after retries.:
        → To: {}
        → Reason: {}
        """, mailTo, e.getMessage());
    }
}
