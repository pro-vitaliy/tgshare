package com.github.provitaliy.node.handler;

import com.github.provitaliy.common.event.EmailAlreadyTakenEvent;
import com.github.provitaliy.common.event.FileReadyEvent;
import com.github.provitaliy.common.event.FileUploadFailedEvent;
import com.github.provitaliy.common.event.UserActivatedEvent;
import com.github.provitaliy.node.bot.BotResponse;
import com.github.provitaliy.node.service.NodeUserCacheService;
import com.github.provitaliy.node.service.NodeUserService;
import com.github.provitaliy.node.service.UserResponseService;
import com.github.provitaliy.node.user.NodeUser;
import com.github.provitaliy.node.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDomainEventHandlerTest {

    @InjectMocks
    private UserDomainEventHandler userDomainEventHandler;

    @Mock
    private NodeUserService userService;

    @Mock
    private NodeUserCacheService cacheService;

    @Mock
    private UserResponseService userResponseService;

    private NodeUser nodeUser;

    @BeforeEach
    void setUp() {
        nodeUser = TestUtils.getActivatedNodeUser();
    }

    @Test
    void shouldHandleUserActivatedEvent() {
        // given
        var event = new UserActivatedEvent(nodeUser.getTelegramUserId(), nodeUser.getEmail());
        nodeUser.setIsActive(false);
        nodeUser.setEmail(null);
        when(userService.getByTelegramUserId(event.telegramUserId())).thenReturn(nodeUser);

        // when
        userDomainEventHandler.handle(event);

        // then
        assertTrue(nodeUser.getIsActive());
        assertEquals(event.email(), nodeUser.getEmail());
        verify(cacheService).save(nodeUser);
        verify(userResponseService).sendUserResponse(
                nodeUser.getChatId(),
                BotResponse.USER_ACTIVATED_RESPONSE
        );
    }

    @Test
    void shouldHandleFileReadyEvent() {
        // given
        var fileUrl = "http://file.url";
        var event = new FileReadyEvent(nodeUser.getTelegramUserId(), fileUrl);
        when(userService.getByTelegramUserId(event.telegramUserId())).thenReturn(nodeUser);

        // when
        userDomainEventHandler.handle(event);

        // then
        verify(userResponseService).sendUserResponse(
                nodeUser.getChatId(),
                BotResponse.FILE_READY_RESPONSE.formatted(fileUrl)
        );
    }

    @Test
    void shouldHandleEmailAlreadyTakenEvent() {
        // given
        var email = "email@ex.io";
        var event = new EmailAlreadyTakenEvent(nodeUser.getTelegramUserId(), email);
        when(userService.getByTelegramUserId(event.telegramUserId())).thenReturn(nodeUser);

        // when
        userDomainEventHandler.handle(event);

        // then
        verify(userResponseService).sendUserResponse(
                nodeUser.getChatId(),
                BotResponse.EMAIL_ALREADY_EXIST.formatted(email)
        );
    }

    @Test
    void shouldHandleFileUploadFailedEvent() {
        // given
        var fileName = "file.txt";
        var event = new FileUploadFailedEvent(nodeUser.getTelegramUserId(), fileName);
        when(userService.getByTelegramUserId(event.telegramUserId())).thenReturn(nodeUser);

        // when
        userDomainEventHandler.handle(event);

        // then
        verify(userResponseService).sendUserResponse(
                nodeUser.getChatId(),
                BotResponse.FILE_UPLOAD_FAILURE_RESPONSE.formatted(fileName)
        );
    }
}
