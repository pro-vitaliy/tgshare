package com.github.provitaliy.notificationservice;

import com.github.provitaliy.notificationservice.service.MailSenderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MailSenderServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private MailSenderService mailSenderService;

    @Test
    void shouldSendMailSuccessfully() {
        String mailTo = "test@example.com";
        String subject = "Test Subject";
        String body = "Test Body";

        mailSenderService.send(mailTo, subject, body);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(captor.capture());
        SimpleMailMessage sentMessage = captor.getValue();

        assertNotNull(sentMessage.getTo());
        assertEquals(mailTo, sentMessage.getTo()[0]);
        assertEquals(subject, sentMessage.getSubject());
        assertEquals(body, sentMessage.getText());
    }

    @Test
    void shouldNotSendMailWhenMailToIsNull() {
        String subject = "Test Subject";
        String body = "Test Body";

        mailSenderService.send(null, subject, body);

        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }
}

