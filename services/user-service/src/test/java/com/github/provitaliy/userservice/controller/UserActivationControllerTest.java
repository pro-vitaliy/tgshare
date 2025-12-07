package com.github.provitaliy.userservice.controller;

import com.github.provitaliy.common.event.UserActivatedEvent;
import com.github.provitaliy.userservice.entity.AppUser;
import com.github.provitaliy.userservice.repository.AppUserRepository;
import com.github.provitaliy.userservice.service.ConsumerService;
import com.github.provitaliy.userservice.service.ProducerService;
import com.github.provitaliy.userservice.util.Encoder;
import com.github.provitaliy.userservice.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class UserActivationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private Encoder idEncoder;

    @MockitoBean
    private ProducerService producerService;

    @MockitoBean
    private ConsumerService consumerService;

    @Value("${service.user-activation.endpoint}")
    private String activationEndpoint;

    @BeforeEach
    void setUp() {
        appUserRepository.deleteAll();
    }

    @Test
    void shouldProduceActivateUserEvent() throws Exception {
        AppUser appUser = TestUtils.randomAppUser();
        appUser = appUserRepository.save(appUser);

        String encodedUserId = idEncoder.encode(appUser.getId());
        var request = get(activationEndpoint + "?id=" + encodedUserId);
        mockMvc.perform(request)
                .andExpect(status().isOk());

        ArgumentCaptor<UserActivatedEvent> userActivatedEventCaptor =
                ArgumentCaptor.forClass(UserActivatedEvent.class);
        verify(producerService).produceUserActivatedEvent(userActivatedEventCaptor.capture());

        AppUser activatedUser = appUserRepository.findByTelegramUserId(appUser.getTelegramUserId()).orElseThrow();
        UserActivatedEvent capturedEvent = userActivatedEventCaptor.getValue();

        assertEquals(appUser.getTelegramUserId(), capturedEvent.telegramUserId());
        assertEquals(appUser.getUnconfirmedEmail(), capturedEvent.email());
        assertTrue(activatedUser.getIsActive());
        assertEquals(appUser.getUnconfirmedEmail(), activatedUser.getEmail());
        assertNull(activatedUser.getUnconfirmedEmail());
    }
}
