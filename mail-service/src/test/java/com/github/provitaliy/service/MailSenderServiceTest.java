package com.github.provitaliy.service;

import com.github.provitaliy.dto.MailParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.any;

@ExtendWith(MockitoExtension.class)
class MailSenderServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private MailSenderService mailSenderService;

    @Captor
    private ArgumentCaptor<SimpleMailMessage> messageCaptor;

    private String mailFrom;
    private String serviceActivationUri;

    @BeforeEach
    void beforeEach() {
        mailFrom = "noreply@ex.io";
        serviceActivationUri = "http://ex.io/activateUser?id={id}";

        ReflectionTestUtils.setField(mailSenderService, "mailFrom", mailFrom);
        ReflectionTestUtils.setField(mailSenderService, "serviceActivationUri", serviceActivationUri);
    }

    @Test
    void shouldSendEmail() {
        MailParams mailParams = new MailParams();
        mailParams.setId("123");
        mailParams.setMailTo("user@ex.io");
        String activationUrl = serviceActivationUri.replace("{id}", "123");

        mailSenderService.send(mailParams);
        verify(mailSender, times(1)).send(messageCaptor.capture());

        SimpleMailMessage messageToSend = messageCaptor.getValue();

        assertEquals(mailFrom, messageToSend.getFrom());
        assertNotNull(messageToSend.getTo());
        assertEquals("user@ex.io", messageToSend.getTo()[0]);
        assertEquals("Активация учетной записи", messageToSend.getSubject());
        assertNotNull(messageToSend.getText());
        assertTrue(messageToSend.getText().contains(activationUrl));
    }

    @Test
    void shouldNotSendEmail() {
        MailParams params = new MailParams(); // mailTo и id — null
        mailSenderService.send(params);
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }
}
