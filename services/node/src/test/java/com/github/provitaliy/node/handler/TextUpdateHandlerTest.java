package com.github.provitaliy.node.handler;

import com.github.provitaliy.node.bot.BotResponse;
import com.github.provitaliy.node.exception.NodeInternalProcessingException;
import com.github.provitaliy.node.service.NodeUserService;
import com.github.provitaliy.node.service.UserResponseService;
import com.github.provitaliy.node.user.NodeUser;
import com.github.provitaliy.node.user.UserState;
import com.github.provitaliy.node.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TextUpdateHandlerTest {

    @InjectMocks
    private TextUpdateHandler textUpdateHandler;

    @Mock
    private NodeUserService userService;

    @Mock
    private UserResponseService userResponseService;

    private NodeUser testUser;

    @BeforeEach
    void setUp() {
        testUser = TestUtils.getActivatedNodeUser();
    }

    @Test
    void shouldHandleCancelCommand() {
        // given
        var cancelUpdate = TestUtils.generateTextMessageDto(testUser, "/cancel");
        when(userService.getOrCreateAppUser(any())).thenReturn(testUser);

        // when
        textUpdateHandler.handleUpdate(cancelUpdate);

        // then
        verify(userService).changeState(testUser, UserState.BASIC_STATE);
        verify(userResponseService).sendUserResponse(testUser.getChatId(), BotResponse.CANCEL_RESPONSE);
    }

    @Test
    void shouldHandleHelpCommand() {
        // given
        var helpUpdate = TestUtils.generateTextMessageDto(testUser, "/help");
        when(userService.getOrCreateAppUser(any())).thenReturn(testUser);

        // when
        textUpdateHandler.handleUpdate(helpUpdate);

        // then
        verify(userResponseService).sendUserResponse(testUser.getChatId(), BotResponse.HELP_RESPONSE);
    }

    @Test
    void shouldHandleStartCommand() {
        // given
        var startUpdate = TestUtils.generateTextMessageDto(testUser, "/start");
        when(userService.getOrCreateAppUser(any())).thenReturn(testUser);

        // when
        textUpdateHandler.handleUpdate(startUpdate);

        // then
        verify(userResponseService).sendUserResponse(testUser.getChatId(), BotResponse.START_RESPONSE);
    }

    @Test
    void shouldHandleUnknownCommand() {
        // given
        var unknownUpdate = TestUtils.generateTextMessageDto(testUser, "unknown");
        when(userService.getOrCreateAppUser(any())).thenReturn(testUser);

        // when
        textUpdateHandler.handleUpdate(unknownUpdate);

        // then
        verify(userResponseService).sendUserResponse(testUser.getChatId(), BotResponse.UNKNOWN_RESPONSE);
    }

    @Test
    void shouldThrowExceptionForUnknownState() {
        // given
        var unknownStateUpdate = TestUtils.generateTextMessageDto(testUser, "some text");
        testUser.setState(null);
        when(userService.getOrCreateAppUser(any())).thenReturn(testUser);

        // when / then
        assertThrows(NodeInternalProcessingException.class, () -> textUpdateHandler.handleUpdate(unknownStateUpdate));
    }

    @Test
    void shouldHandleRegistrationCommand() {
        // given
        testUser.setIsActive(false);
        var registrationUpdate = TestUtils.generateTextMessageDto(testUser, "/registration");
        when(userService.getOrCreateAppUser(any())).thenReturn(testUser);

        // when
        textUpdateHandler.handleUpdate(registrationUpdate);

        // then
        verify(userService).changeState(testUser, UserState.WAIT_FOR_EMAIL_STATE);
        verify(userResponseService).sendUserResponse(testUser.getChatId(), BotResponse.WAIT_FOR_EMAIL_RESPONSE);
    }

    @Test
    void shouldHandleAlreadyActivatedUserRegisterCommand() {
        // given
        var registrationUpdate = TestUtils.generateTextMessageDto(testUser, "/registration");
        when(userService.getOrCreateAppUser(any())).thenReturn(testUser);

        // when
        textUpdateHandler.handleUpdate(registrationUpdate);

        // then
        verify(userResponseService).sendUserResponse(testUser.getChatId(), BotResponse.ALREADY_REGISTERED_RESPONSE);
        verify(userService).getOrCreateAppUser(any());
        verifyNoMoreInteractions(userService);
    }

    @Test
    void shouldSendEmailConfirmationResponse() {
        // given
        String email = " Test@ex.io";
        testUser.setState(UserState.WAIT_FOR_EMAIL_STATE);
        var emailUpdate = TestUtils.generateTextMessageDto(testUser, email);
        when(userService.getOrCreateAppUser(any())).thenReturn(testUser);

        // when
        textUpdateHandler.handleUpdate(emailUpdate);

        // then
        verify(userService).setEmail(eq(testUser), eq("test@ex.io"));
        verify(userService).changeState(testUser, UserState.BASIC_STATE);
        verify(userResponseService).sendUserResponse(testUser.getChatId(), BotResponse.EMAIL_CONFIRMATION_RESPONSE);
    }

    @Test
    void shouldSendIncorrectEmailResponse() {
        // given
        String email = "invalid-email";
        testUser.setState(UserState.WAIT_FOR_EMAIL_STATE);
        var emailUpdate = TestUtils.generateTextMessageDto(testUser, email);
        when(userService.getOrCreateAppUser(any())).thenReturn(testUser);

        // when
        textUpdateHandler.handleUpdate(emailUpdate);

        // then
        verify(userResponseService).sendUserResponse(testUser.getChatId(), BotResponse.INCORRECT_EMAIL_RESPONSE);
        verify(userService).getOrCreateAppUser(any());
        verifyNoMoreInteractions(userService);
    }

    @Test
    void shouldDoNothingForNullText() {
        var nullTextUpdate = TestUtils.generateTextMessageDto(testUser, null);
        textUpdateHandler.handleUpdate(nullTextUpdate);

        textUpdateHandler.handleUpdate(null);
        verifyNoInteractions(userService, userResponseService);
    }

    @Test
    void shouldDoNothingForNullMessage() {
        textUpdateHandler.handleUpdate(null);
        verifyNoInteractions(userService, userResponseService);
    }
}
