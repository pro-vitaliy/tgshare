package com.github.provitaliy.node.handler;

import com.github.provitaliy.common.dto.telegram.TelegramDocumentMessageDto;
import com.github.provitaliy.common.dto.telegram.TelegramPhotoMessageDto;
import com.github.provitaliy.common.event.FileUploadEvent;
import com.github.provitaliy.node.bot.BotResponse;
import com.github.provitaliy.node.service.NodeUserService;
import com.github.provitaliy.node.service.ProducerService;
import com.github.provitaliy.node.service.UserResponseService;
import com.github.provitaliy.node.user.NodeUser;
import com.github.provitaliy.node.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileUpdateHandlerTest {

    @InjectMocks
    private FileUpdateHandler fileUpdateHandler;

    @Mock
    private NodeUserService userService;

    @Mock
    private ProducerService producerService;

    @Mock
    private UserResponseService userResponseService;

    @Captor
    ArgumentCaptor<FileUploadEvent> eventCaptor;

    private NodeUser testUser;

    @BeforeEach
    public void setUp() {
        testUser = TestUtils.getActivatedNodeUser();
    }

    @Test
    void shouldProcessDocUpdateSuccessfully() {
        // given
        var docMessage = TestUtils.generateDocMessageDto(testUser);
        when(userService.getOrCreateAppUser(any())).thenReturn(testUser);

        // when
        fileUpdateHandler.handleDocUpdate(docMessage);

        // then
        verify(userResponseService).sendUserResponse(testUser.getChatId(), BotResponse.FILE_RECEIVED_RESPONSE);
        verify(producerService).produceFileUploadRequest(eventCaptor.capture());
        FileUploadEvent capturedEvent = eventCaptor.getValue();

        assertEquals(docMessage.documentId(), capturedEvent.getTelegramFileId());
        assertEquals(testUser.getTelegramUserId(), capturedEvent.getTelegramUserId());
    }

    @Test
    void shouldReturnForbiddenWhenUserIsNotActiveInDocUpdate() {
        // given
        var docMessage = TestUtils.generateDocMessageDto(testUser);
        testUser.setIsActive(false);
        when(userService.getOrCreateAppUser(any())).thenReturn(testUser);

        // when
        fileUpdateHandler.handleDocUpdate(docMessage);

        // then
        verify(userResponseService).sendUserResponse(testUser.getChatId(), BotResponse.NOT_ALLOW_TO_SEND_FILE_RESPONSE);
        verifyNoInteractions(producerService);
    }

    @Test
    void shouldProcessPhotoUpdateSuccessfully() {
        // given
        var photoMessage = TestUtils.generatePhotoMessageDto(testUser);
        when(userService.getOrCreateAppUser(any())).thenReturn(testUser);

        // when
        fileUpdateHandler.handlePhotoUpdate(photoMessage);

        // then
        verify(userResponseService).sendUserResponse(testUser.getChatId(), BotResponse.FILE_RECEIVED_RESPONSE);
        verify(producerService).produceFileUploadRequest(eventCaptor.capture());
        FileUploadEvent capturedEvent = eventCaptor.getValue();

        assertEquals(photoMessage.photoId(), capturedEvent.getTelegramFileId());
        assertEquals(testUser.getTelegramUserId(), capturedEvent.getTelegramUserId());
    }

    @Test
    void shouldReturnForbiddenWhenUserIsNotActiveInPhotoUpdate() {
        // given
        var photoMessage = TestUtils.generatePhotoMessageDto(testUser);
        testUser.setIsActive(false);
        when(userService.getOrCreateAppUser(any())).thenReturn(testUser);

        // when
        fileUpdateHandler.handlePhotoUpdate(photoMessage);

        // then
        verify(userResponseService).sendUserResponse(testUser.getChatId(), BotResponse.NOT_ALLOW_TO_SEND_FILE_RESPONSE);
        verifyNoInteractions(producerService);
    }

    @Test
    void shouldDoNothingWhenPhotoMessageIsNull() {
        fileUpdateHandler.handlePhotoUpdate(null);
        verifyNoInteractions(userService, producerService, userResponseService);
    }

    @Test
    void shouldDoNothingWhenDocMessageIsNull() {
        fileUpdateHandler.handleDocUpdate(null);
        verifyNoInteractions(userService, producerService, userResponseService);
    }



    @Test
    void shouldDoNothingWhenDocumentIdIsNull() {
        // given
        var docMessage = new TelegramDocumentMessageDto(
                testUser.getChatId(),
                TestUtils.generateTelegramUserDto(testUser),
                null,
                "test.pdf",
                "application/pdf",
                12345L
        );

        // when
        fileUpdateHandler.handleDocUpdate(docMessage);

        // then
        verifyNoInteractions(userService, producerService, userResponseService);
    }

    @Test
    void shouldDoNothingWhenPhotoIdIsNull() {
        // given
        var photoMessage = new TelegramPhotoMessageDto(
                testUser.getChatId(),
                TestUtils.generateTelegramUserDto(testUser),
                null,
                12345L
        );

        // when
        fileUpdateHandler.handlePhotoUpdate(photoMessage);

        // then
        verifyNoInteractions(userService, producerService, userResponseService);
    }
}
