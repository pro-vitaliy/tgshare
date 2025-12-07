package com.github.provitaliy.userservice.service;

import com.github.provitaliy.common.event.EmailAlreadyTakenEvent;
import com.github.provitaliy.common.event.SendEmailEvent;
import com.github.provitaliy.common.event.UserEmailEnteredEvent;
import com.github.provitaliy.userservice.entity.AppUser;
import com.github.provitaliy.userservice.repository.AppUserRepository;
import com.github.provitaliy.userservice.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ActiveProfiles("test")
@SpringBootTest
class ConsumerServiceTest {

    @Autowired
    private ConsumerService consumerService;

    @MockitoBean
    private ProducerService producerService;

    @Autowired
    private AppUserRepository appUserRepository;

    @BeforeEach
    public void setUp() {
        appUserRepository.deleteAll();
    }

    @Test
    void shouldProduceSendMailEvent() {
        UserEmailEnteredEvent event = TestUtils.generateUserEmailEnteredEvent();
        AppUser existingUser = TestUtils.randomAppUser();
        event.setTelegramUserId(existingUser.getTelegramUserId());
        appUserRepository.save(existingUser);

        consumerService.consumeUserEmailEnteredEvent(event);
        ArgumentCaptor<SendEmailEvent> eventCaptor = ArgumentCaptor.forClass(SendEmailEvent.class);
        verify(producerService).produceSendMailEvent(eventCaptor.capture());
        SendEmailEvent capturedEvent = eventCaptor.getValue();

        assertEquals(event.getEmail(), capturedEvent.getMailTo());
    }

    @Test
    void shouldProduceEmailAlreadyTakenEvent() {
        UserEmailEnteredEvent event = TestUtils.generateUserEmailEnteredEvent();
        AppUser existingUser = TestUtils.randomAppUser();
        existingUser.setIsActive(true);
        existingUser.setEmail(event.getEmail());
        appUserRepository.save(existingUser);

        consumerService.consumeUserEmailEnteredEvent(event);

        ArgumentCaptor<EmailAlreadyTakenEvent> eventCaptor = ArgumentCaptor.forClass(EmailAlreadyTakenEvent.class);
        verify(producerService).produceEmailAlreadyTakenEvent(eventCaptor.capture());
        EmailAlreadyTakenEvent capturedEvent = eventCaptor.getValue();

        assertEquals(event.getEmail(), capturedEvent.email());
        assertEquals(event.getTelegramUserId(), capturedEvent.telegramUserId());
    }
}
