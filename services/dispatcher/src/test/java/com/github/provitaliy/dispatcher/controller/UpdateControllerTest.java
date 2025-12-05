package com.github.provitaliy.dispatcher.controller;

import com.github.provitaliy.common.dto.telegram.TelegramDocumentMessageDto;
import com.github.provitaliy.common.dto.telegram.TelegramPhotoMessageDto;
import com.github.provitaliy.common.dto.telegram.TelegramTextMessageDto;
import com.github.provitaliy.dispatcher.mapper.TelegramMessageMapper;
import com.github.provitaliy.dispatcher.service.UpdateProducer;
import com.github.provitaliy.dispatcher.util.TestDtoFactory;
import com.github.provitaliy.dispatcher.util.TestUpdateFactory;
import com.github.provitaliy.dispatcher.utils.MessageUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateControllerTest {

    @Mock
    private TelegramClient telegramClient;

    @Mock
    private UpdateProducer updateProducer;

    @Mock
    private TelegramMessageMapper messageMapper;

    @InjectMocks
    private UpdateController updateController;

    @Captor
    private ArgumentCaptor<TelegramTextMessageDto> textMessageDtoCaptor;

    @Captor
    private ArgumentCaptor<TelegramDocumentMessageDto> docMessageDtoCaptor;

    @Captor
    private ArgumentCaptor<TelegramPhotoMessageDto> photoMessageDtoCaptor;

    @Captor
    private ArgumentCaptor<SendMessage> sendMessageCaptor;

    @Test
    void shouldProduceTextMessageToQueue() {
        var update = TestUpdateFactory.textUpdate("test message");
        var expectedDto = TestDtoFactory.textDtoFrom(update);

        when(messageMapper.toTextMessageDto(update.getMessage()))
                .thenReturn(expectedDto);

        updateController.processUpdate(update);
        verify(updateProducer).produceTextMessageUpdate(textMessageDtoCaptor.capture());
        TelegramTextMessageDto capturedDto = textMessageDtoCaptor.getValue();

        assertEquals(expectedDto, capturedDto);
    }

    @Test
    void shouldProduceDocMessageToQueue() {
        Update docUpdate = TestUpdateFactory.docUpdate();
        var expectedDto = TestDtoFactory.docDtoFrom(docUpdate);

        when(messageMapper.toDocumentMessageDto(docUpdate.getMessage()))
                .thenReturn(expectedDto);

        updateController.processUpdate(docUpdate);
        verify(updateProducer).produceDocMessageUpdate(docMessageDtoCaptor.capture());
        TelegramDocumentMessageDto docMessageToProduce = docMessageDtoCaptor.getValue();

        assertEquals(expectedDto, docMessageToProduce);
    }

    @Test
    void shouldProducePhotoMessageToQueue() {
        Update photoUpdate = TestUpdateFactory.photoUpdate();
        var expectedDto = TestDtoFactory.photoDtoFrom(photoUpdate);

        when(messageMapper.toPhotoMessageDto(photoUpdate.getMessage()))
                .thenReturn(expectedDto);

        updateController.processUpdate(photoUpdate);
        verify(updateProducer).producePhotoMessageUpdate(photoMessageDtoCaptor.capture());
        TelegramPhotoMessageDto photoMessageToProduce = photoMessageDtoCaptor.getValue();

        assertEquals(expectedDto, photoMessageToProduce);
    }

    @Test
    void shouldSendUnsupportedMessageWarningViaTelegram() throws Exception {
        Update unsupportedUpdate = TestUpdateFactory.textUpdate(null);
        String expectedResponse = MessageUtils.UNSUPPORTED_MESSAGE_TYPE_RESPONSE;

        updateController.processUpdate(unsupportedUpdate);
        verify(telegramClient).execute(sendMessageCaptor.capture());
        SendMessage messageToSend = sendMessageCaptor.getValue();

        assertEquals(expectedResponse, messageToSend.getText());
    }

    @Test
    void shouldIgnoreNullUpdate() {
        updateController.processUpdate(null);
        verifyNoInteractions(updateProducer, telegramClient);
    }
}
